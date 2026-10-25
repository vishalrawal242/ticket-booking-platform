package com.vishal.ticketbooking.dto;

import com.vishal.ticketbooking.entity.Booking;
import com.vishal.ticketbooking.entity.BookingStatus;
import com.vishal.ticketbooking.entity.SeatStatus;

import java.time.LocalDateTime;

public record BookingResponse(
        Long bookingId,
        Long userId,
        Long seatId,
        String seatNumber,
        BookingStatus bookingStatus,
        SeatStatus seatStatus,
        LocalDateTime createdAt) {

    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getUserId(),
                booking.getSeat().getId(),
                booking.getSeat().getSeatNumber(),
                booking.getBookingStatus(),
                booking.getSeat().getStatus(),
                booking.getCreatedAt());
    }
}