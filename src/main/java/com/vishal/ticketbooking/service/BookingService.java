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

    public BookingService(BookingRepository bookingRepository, SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {

        Seat seat = seatRepository.findByIdForUpdate(request.seatId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Seat not found with id " + request.seatId()));

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new SeatNotAvailableException(
                    "Seat " + seat.getSeatNumber() + " is already booked");
        }

        seat.setStatus(SeatStatus.BOOKED);
        seatRepository.save(seat);

        Booking booking = bookingRepository.save(
                new Booking(request.userId(), seat, BookingStatus.CONFIRMED));

        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id " + bookingId));
        return BookingResponse.from(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id " + bookingId));

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new InvalidBookingStateException(
                    "Booking " + bookingId + " is already " + booking.getBookingStatus());
        }

        Seat seat = seatRepository.findByIdForUpdate(booking.getSeat().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

        seat.setStatus(SeatStatus.AVAILABLE);
        booking.setBookingStatus(BookingStatus.CANCELLED);
        seatRepository.save(seat);
        bookingRepository.save(booking);

        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getUserBookings(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(BookingResponse::from).toList();
    }
}