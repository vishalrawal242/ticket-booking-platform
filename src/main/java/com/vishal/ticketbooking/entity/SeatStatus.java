package com.vishal.ticketbooking.entity;

// An enum is a fixed list of allowed values.
// A seat can ONLY be in one of these 3 states, so typos like "avalable" are impossible.
public enum SeatStatus {
    AVAILABLE, // seat is free and can be reserved
    LOCKED,    // seat is temporarily held while a booking is in progress
    BOOKED     // seat is confirmed and taken
}