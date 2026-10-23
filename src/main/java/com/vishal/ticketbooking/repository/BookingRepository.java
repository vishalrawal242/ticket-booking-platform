package com.vishal.ticketbooking.repository;

import com.vishal.ticketbooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Spring builds the SQL from the method name:
    // SELECT * FROM bookings WHERE user_id = ? ORDER BY created_at DESC
    // Used by the "user booking history" endpoint (newest booking first).
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
}