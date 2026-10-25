package com.vishal.ticketbooking.dto;

import jakarta.validation.constraints.NotNull;

public record BookingRequest(
        @NotNull(message = "userId is required") Long userId,
        @NotNull(message = "seatId is required") Long seatId) {
}