package com.vishal.ticketbooking.entity;

// The possible results/states of a booking.
public enum BookingStatus {
    CONFIRMED, // booking succeeded, seat is yours
    FAILED,    // booking attempt failed (e.g. seat was already taken)
    CANCELLED  // user cancelled, seat is released
}