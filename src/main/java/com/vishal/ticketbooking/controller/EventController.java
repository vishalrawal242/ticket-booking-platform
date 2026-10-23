package com.vishal.ticketbooking.controller;

import com.vishal.ticketbooking.dto.EventResponse;
import com.vishal.ticketbooking.dto.SeatResponse;
import com.vishal.ticketbooking.service.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// @RestController = this class handles web requests and returns JSON.
@RestController
// Every URL in this class starts with /api/v1/events.
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    // The controller only talks to the service, never directly to repositories.
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // ENDPOINT 1: GET /api/v1/events -> list of all events.
    @GetMapping
    public List<EventResponse> getAllEvents() {
        return eventService.getAllEvents();
    }

    // ENDPOINT 2: GET /api/v1/events/{eventId}/seats -> seats of one event.
    // @PathVariable takes the {eventId} part of the URL and gives it to the method.
    @GetMapping("/{eventId}/seats")
    public List<SeatResponse> getSeats(@PathVariable Long eventId) {
        return eventService.getSeatsForEvent(eventId);
    }
}