package com.vishal.ticketbooking.exception;

// Thrown for illegal actions on a booking, e.g. cancelling a booking
// that is already cancelled. (We'll use this in the cancel endpoint.)
public class InvalidBookingStateException extends RuntimeException {
    public InvalidBookingStateException(String message) {
        super(message);
    }
}