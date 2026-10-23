package com.vishal.ticketbooking.repository;

import com.vishal.ticketbooking.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<Event, Long> = "a repository for the Event entity whose ID type is Long".
// Spring writes the implementation for us at startup. We get save(), findById(),
// findAll(), delete(), count() etc. without writing any SQL.
public interface EventRepository extends JpaRepository<Event, Long> {
}