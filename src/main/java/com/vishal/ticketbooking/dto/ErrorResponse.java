package com.vishal.ticketbooking.dto;

import java.time.LocalDateTime;

// The standard JSON shape for every error: status code, short title, message, time.
public record ErrorResponse(int status, String error, String message, LocalDateTime timestamp) {
}