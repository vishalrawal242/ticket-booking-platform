package com.vishal.ticketbooking.dto;

import jakarta.validation.constraints.NotNull;

// The JSON body the client sends to book a seat: {"userId": 1, "seatId": 3}
// @NotNull makes Spring reject the request with an error if a field is missing.
public record BookingRequest(
        @NotNull(message = "userId is required") Long userId,
        @NotNull(message = "seatId is required") Long seatId) {
}