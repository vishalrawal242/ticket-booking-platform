package com.vishal.ticketbooking.service;

import com.vishal.ticketbooking.dto.EventResponse;
import com.vishal.ticketbooking.dto.SeatResponse;
import com.vishal.ticketbooking.exception.ResourceNotFoundException;
import com.vishal.ticketbooking.repository.EventRepository;
import com.vishal.ticketbooking.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @Service = Spring manages this class. The service layer holds the business logic.
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    // Constructor injection: Spring supplies the repositories.
    public EventService(EventRepository eventRepository, SeatRepository seatRepository) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
    }

    // readOnly = true tells the database we only read here (a small performance gain).
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        // Fetch all events, convert each one to an EventResponse DTO.
        return eventRepository.findAll().stream().map(EventResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> getSeatsForEvent(Long eventId) {
        // First make sure the event exists, otherwise return a 404 error.
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with id " + eventId);
        }
        // Fetch the seats of that event and convert them to DTOs.
        return seatRepository.findByEventId(eventId).stream().map(SeatResponse::from).toList();
    }
}