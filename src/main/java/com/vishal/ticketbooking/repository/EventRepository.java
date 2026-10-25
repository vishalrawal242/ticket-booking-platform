package com.vishal.ticketbooking.repository;

import com.vishal.ticketbooking.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}