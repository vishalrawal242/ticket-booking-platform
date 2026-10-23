package com.vishal.ticketbooking.dto;

import com.vishal.ticketbooking.entity.Event;
import java.time.LocalDateTime;

// A record is a short way to write an immutable data class.
// Java auto-creates the constructor, getters, equals() and toString().
// This is the event data we send back to the client.
public record EventResponse(Long id, String name, String venue, LocalDateTime eventTime) {

    // Converts a database Event entity into an EventResponse.
    // "static" = we can call it without creating an object: EventResponse.from(event)
    public static EventResponse from(Event event) {
        return new EventResponse(event.getId(), event.getName(), event.getVenue(), event.getEventTime());
    }
}