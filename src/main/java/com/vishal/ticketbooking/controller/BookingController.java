package com.vishal.ticketbooking.controller;

import com.vishal.ticketbooking.dto.BookingRequest;
import com.vishal.ticketbooking.dto.BookingResponse;
import com.vishal.ticketbooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// Common URL start for every method in this class.
@RequestMapping("/api/v1")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // ENDPOINT 3: POST /api/v1/bookings -> reserve a seat.
    // @RequestBody = read the JSON sent by the client into a BookingRequest.
    // @Valid = run the @NotNull checks we added in BookingRequest.
    // 201 CREATED is the correct success code when something new is created.
    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody BookingRequest request) {
        return bookingService.createBooking(request);
    }

    // ENDPOINT 4: GET /api/v1/bookings/{bookingId} -> booking confirmation + seat status.
    @GetMapping("/bookings/{bookingId}")
    public BookingResponse getBooking(@PathVariable Long bookingId) {
        return bookingService.getBooking(bookingId);
    }

    // ENDPOINT 5: POST /api/v1/bookings/{bookingId}/cancel -> cancel and release the seat.
    @PostMapping("/bookings/{bookingId}/cancel")
    public BookingResponse cancelBooking(@PathVariable Long bookingId) {
        return bookingService.cancelBooking(bookingId);
    }

    // ENDPOINT 6: GET /api/v1/users/{userId}/bookings -> user's booking history.
    @GetMapping("/users/{userId}/bookings")
    public List<BookingResponse> getUserBookings(@PathVariable Long userId) {
        return bookingService.getUserBookings(userId);
    }
}