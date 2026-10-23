package com.vishal.ticketbooking;

import com.vishal.ticketbooking.dto.BookingRequest;
import com.vishal.ticketbooking.entity.Event;
import com.vishal.ticketbooking.entity.Seat;
import com.vishal.ticketbooking.entity.SeatStatus;
import com.vishal.ticketbooking.exception.SeatNotAvailableException;
import com.vishal.ticketbooking.repository.BookingRepository;
import com.vishal.ticketbooking.repository.EventRepository;
import com.vishal.ticketbooking.repository.SeatRepository;
import com.vishal.ticketbooking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// @SpringBootTest = starts the FULL Spring application (real services + real database).
// @ActiveProfiles("test") = load application-test.properties (the test database).
@SpringBootTest
@ActiveProfiles("test")
class BookingConcurrencyTest {

    // Static block runs once when the class loads, BEFORE Spring connects to PostgreSQL.
    // Tests don't run main(), so we set the timezone here (same fix as in main()).
    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }

    // Spring gives us the real objects to use inside the test.
    @Autowired
    private BookingService bookingService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private BookingRepository bookingRepository;

    // Test 1: the exact scenario from your resume - 5 concurrent requests, 1 seat.
    @Test
    void fiveConcurrentRequests_onlyOneBookingSucceeds() throws Exception {
        runConcurrentBookingTest(5);
    }

    // Test 2: heavier load - 20 concurrent requests, 1 seat.
    @Test
    void twentyConcurrentRequests_onlyOneBookingSucceeds() throws Exception {
        runConcurrentBookingTest(20);
    }

    // Shared logic: many threads try to book the SAME seat at the SAME moment.
    private void runConcurrentBookingTest(int threadCount) throws Exception {

        // ARRANGE: create a fresh event and ONE available seat just for this test.
        Event event = eventRepository.save(
                new Event("Test Event", "Test Arena", LocalDateTime.now().plusDays(1)));
        Seat seat = seatRepository.save(new Seat(event, "T-" + System.nanoTime()));
        long bookingsBefore = bookingRepository.count();

        // A pool of worker threads, one per simulated user.
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // readyLatch: each thread says "I'm ready" so we know all are waiting.
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        // startLatch: the "starting gun" - all threads wait on it, then fire together.
        CountDownLatch startLatch = new CountDownLatch(1);
        // doneLatch: each thread says "I'm finished" so we know when to check results.
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        // Thread-safe counters (normal ints are NOT safe when many threads update them).
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger seatTakenCount = new AtomicInteger();
        AtomicInteger unexpectedErrorCount = new AtomicInteger();

        // ACT: start all the threads.
        for (int i = 1; i <= threadCount; i++) {
            long userId = i; // each thread is a different user
            executor.submit(() -> {
                readyLatch.countDown();       // "I'm ready"
                try {
                    startLatch.await();       // wait for the starting gun
                    bookingService.createBooking(new BookingRequest(userId, seat.getId()));
                    successCount.incrementAndGet();       // booking worked
                } catch (SeatNotAvailableException e) {
                    seatTakenCount.incrementAndGet();     // lost the race (expected)
                } catch (Exception e) {
                    unexpectedErrorCount.incrementAndGet(); // anything else is a problem
                } finally {
                    doneLatch.countDown();    // "I'm finished"
                }
            });
        }

        // Wait until every thread is ready, then fire the starting gun.
        readyLatch.await();
        startLatch.countDown();

        // Wait (max 30 seconds) for every thread to finish, then shut the pool down.
        boolean allFinished = doneLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // ASSERT: check the results.
        assertTrue(allFinished, "Threads did not finish in time");
        // Exactly ONE user got the seat.
        assertEquals(1, successCount.get(), "Exactly one booking must succeed");
        // Everyone else was rejected cleanly with "seat not available".
        assertEquals(threadCount - 1, seatTakenCount.get(), "All others must be rejected");
        assertEquals(0, unexpectedErrorCount.get(), "No unexpected errors allowed");
        // The database has exactly ONE new booking row (zero double-bookings).
        assertEquals(bookingsBefore + 1, bookingRepository.count());
        // The seat is BOOKED in the database.
        assertEquals(SeatStatus.BOOKED, seatRepository.findById(seat.getId()).get().getStatus());
    }
}