package com.vishal.ticketbooking.dto;

import com.vishal.ticketbooking.entity.Seat;
import com.vishal.ticketbooking.entity.SeatStatus;

// The seat data we send back to the client (id, label like "A1", and status).
public record SeatResponse(Long id, String seatNumber, SeatStatus status) {

    // Converts a Seat entity into a SeatResponse.
    public static SeatResponse from(Seat seat) {
        return new SeatResponse(seat.getId(), seat.getSeatNumber(), seat.getStatus());
    }
}