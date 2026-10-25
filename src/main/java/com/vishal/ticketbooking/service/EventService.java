package com.vishal.ticketbooking.service;

import com.vishal.ticketbooking.dto.EventResponse;
import com.vishal.ticketbooking.dto.SeatResponse;
import com.vishal.ticketbooking.exception.ResourceNotFoundException;
import com.vishal.ticketbooking.repository.EventRepository;
import com.vishal.ticketbooking.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    public EventService(EventRepository eventRepository, SeatRepository seatRepository) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream().map(EventResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> getSeatsForEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with id " + eventId);
        }
        return seatRepository.findByEventId(eventId).stream().map(SeatResponse::from).toList();
    }
}