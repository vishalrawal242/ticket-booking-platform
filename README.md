# Concurrent Ticket Booking Platform

A ticket booking backend built with Java 21 and Spring Boot. It handles many users trying to reserve seats at the same time without ever double-booking a seat.

## Tech Stack

- Java 21, Spring Boot, Spring Data JPA, Hibernate
- PostgreSQL (running in Docker)
- JUnit 5, Spring Boot Test
- Postman (API testing)
- Maven

## Features

- Three tables: Event, Seat and Booking
- 7 REST endpoints for events, seats and bookings
- Pessimistic locking (PESSIMISTIC_WRITE) with explicit @Transactional, so only one request can reserve a seat at a time
- Multi-threaded integration tests using ExecutorService, run against a PostgreSQL Docker container
- Verified: 5 concurrent reservation requests for the same seat resulted in 0 double-bookings

## Author

Vishal Rawal