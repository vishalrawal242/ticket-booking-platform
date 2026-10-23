package com.vishal.ticketbooking.exception;

import com.vishal.ticketbooking.dto.ErrorResponse;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

// @RestControllerAdvice = one central place that catches exceptions thrown
// by ANY controller and turns them into clean JSON error responses.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Item not found -> HTTP 404.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Seat already taken -> HTTP 409 Conflict (the correct code for "someone else got it first").
    @ExceptionHandler(SeatNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleSeatNotAvailable(SeatNotAvailableException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Illegal booking action (e.g. double cancel) -> HTTP 409 Conflict.
    @ExceptionHandler(InvalidBookingStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState(InvalidBookingStateException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    // The database couldn't get the row lock in time (too much traffic) or hit a lock failure.
    // We tell the client to retry -> HTTP 409.
    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleLockFailure(PessimisticLockingFailureException ex) {
        return build(HttpStatus.CONFLICT, "Seat is being booked by someone else. Please retry.");
    }

    // Request body failed validation (e.g. missing userId) -> HTTP 400 Bad Request.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        // Pick the first validation message, e.g. "userId is required".
        String message = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return build(HttpStatus.BAD_REQUEST, message);
    }

    // Helper used by all handlers above so we build errors in one consistent shape.
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        ErrorResponse body = new ErrorResponse(
                status.value(), status.getReasonPhrase(), message, LocalDateTime.now());
        return ResponseEntity.status(status).body(body);
    }
}