package com.vishal.ticketbooking.exception;

// Thrown when someone tries to book a seat that is already LOCKED or BOOKED.
// This is the main "race condition lost" error in flash-sale traffic.
public class SeatNotAvailableException extends RuntimeException {
    public SeatNotAvailableException(String message) {
        super(message);
    }
}