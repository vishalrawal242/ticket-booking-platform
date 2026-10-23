package com.vishal.ticketbooking.service;

import com.vishal.ticketbooking.dto.BookingRequest;
import com.vishal.ticketbooking.dto.BookingResponse;
import com.vishal.ticketbooking.entity.Booking;
import com.vishal.ticketbooking.entity.BookingStatus;
import com.vishal.ticketbooking.entity.Seat;
import com.vishal.ticketbooking.entity.SeatStatus;
import com.vishal.ticketbooking.exception.InvalidBookingStateException;
import com.vishal.ticketbooking.exception.ResourceNotFoundException;
import com.vishal.ticketbooking.exception.SeatNotAvailableException;
import com.vishal.ticketbooking.repository.BookingRepository;
import com.vishal.ticketbooking.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;

    // Constructor injection: Spring supplies both repositories.
    public BookingService(BookingRepository bookingRepository, SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
    }

    // THE MOST IMPORTANT METHOD OF THE PROJECT.
    // @Transactional = everything inside runs as ONE database transaction.
    // If any exception is thrown, ALL changes are rolled back (no half-saved data).
    // The row lock taken below is held until this method finishes.
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {

        // 1. Fetch the seat WITH a pessimistic lock (SELECT ... FOR UPDATE).
        //    If another request holds the lock on this seat, we WAIT here until it finishes.
        //    So only ONE request at a time can check and change this seat.
        Seat seat = seatRepository.findByIdForUpdate(request.seatId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seat not found with id " + request.seatId()));

        // 2. Now that we own the lock, check the seat is still free.
        //    A request that waited will see BOOKED here and fail -> no double booking.
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new SeatNotAvailableException(
                    "Seat " + seat.getSeatNumber() + " is already booked");
        }

        // 3. Mark the seat as taken.
        seat.setStatus(SeatStatus.BOOKED);
        seatRepository.save(seat);

        // 4. Create and save the booking record as CONFIRMED.
        Booking booking = bookingRepository.save(
                new Booking(request.userId(), seat, BookingStatus.CONFIRMED));

        // 5. Convert to a DTO and return. The lock is released when the transaction commits.
        return BookingResponse.from(booking);
    }

    // Read-only lookup of one booking by its id.
    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id " + bookingId));
        return BookingResponse.from(booking);
    }

    // Cancels a booking and frees the seat again.
    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id " + bookingId));

        // Only a CONFIRMED booking can be cancelled (stops double-cancelling).
        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new InvalidBookingStateException(
                    "Booking " + bookingId + " is already " + booking.getBookingStatus());
        }

        // Lock the seat before changing it, so a cancel can't clash with a new booking.
        Seat seat = seatRepository.findByIdForUpdate(booking.getSeat().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

        // Release the seat and mark the booking cancelled.
        seat.setStatus(SeatStatus.AVAILABLE);
        booking.setBookingStatus(BookingStatus.CANCELLED);
        seatRepository.save(seat);
        bookingRepository.save(booking);

        return BookingResponse.from(booking);
    }

    // Booking history of one user, newest first.
    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(BookingResponse::from).toList();
    }
}