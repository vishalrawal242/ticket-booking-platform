package com.vishal.ticketbooking.controller;

import com.vishal.ticketbooking.dto.EventResponse;
import com.vishal.ticketbooking.dto.SeatResponse;
import com.vishal.ticketbooking.service.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventResponse> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{eventId}/seats")
    public List<SeatResponse> getSeats(@PathVariable Long eventId) {
        return eventService.getSeatsForEvent(eventId);
    }
}