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
- Verified: 100 concurrent reservation requests for the same seat resulted in 0 double-bookings

## How to Run

You need Java and Docker Desktop installed. Make sure Docker Desktop is running, then:

Start PostgreSQL:

```
docker compose up -d
```

Start the application:

```
./mvnw spring-boot:run
```

Run the tests (the database must be running):

```
./mvnw test
```

## API Endpoints

Base URL: `http://localhost:8080/api/v1`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Check that the app and database are up |
| GET | `/events` | List all events |
| GET | `/events/{eventId}/seats` | List the seats of an event |
| POST | `/bookings` | Book a seat |
| GET | `/bookings/{bookingId}` | Get one booking |
| POST | `/bookings/{bookingId}/cancel` | Cancel a booking |
| GET | `/users/{userId}/bookings` | List a user's bookings |

## API Examples

### List events

`GET /api/v1/events`

```json
[
  {
    "id": 1,
    "name": "Flash Sale Concert",
    "venue": "Pune Arena",
    "eventTime": "2026-12-15T16:50:02.893301"
  }
]
```

### List seats of an event

`GET /api/v1/events/1/seats`

The event has 10 seats (A1 to A10). The first two are shown here.

```json
[
  { "id": 1, "seatNumber": "A1", "status": "AVAILABLE" },
  { "id": 10, "seatNumber": "A10", "status": "AVAILABLE" }
]
```

### Book a seat

`POST /api/v1/bookings`

```bash
curl -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "seatId": 1}'
```

Response: `201 Created`

```json
{
  "bookingId": 1,
  "userId": 1,
  "seatId": 1,
  "seatNumber": "A1",
  "bookingStatus": "CONFIRMED",
  "seatStatus": "BOOKED",
  "createdAt": "2026-11-15T17:10:43.5106769"
}
```

## Author

Vishal Rawal