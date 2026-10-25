package com.vishal.ticketbooking.dto;

import com.vishal.ticketbooking.entity.Event;
import java.time.LocalDateTime;

public record EventResponse(Long id, String name, String venue, LocalDateTime eventTime) {
    public static EventResponse from(Event event) {
        return new EventResponse(event.getId(), event.getName(), event.getVenue(), event.getEventTime());
    }
}