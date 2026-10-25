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

@SpringBootTest
@ActiveProfiles("test")
class BookingConcurrencyTest {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }

    @Autowired
    private BookingService bookingService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void fiveConcurrentRequests_onlyOneBookingSucceeds() throws Exception {
        runConcurrentBookingTest(5);
    }

    @Test
    void twentyConcurrentRequests_onlyOneBookingSucceeds() throws Exception {
        runConcurrentBookingTest(20);
    }

    private void runConcurrentBookingTest(int threadCount) throws Exception {

        Event event = eventRepository.save(
                new Event("Test Event", "Test Arena", LocalDateTime.now().plusDays(1)));
        Seat seat = seatRepository.save(new Seat(event, "T-" + System.nanoTime()));
        long bookingsBefore = bookingRepository.count();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger seatTakenCount = new AtomicInteger();
        AtomicInteger unexpectedErrorCount = new AtomicInteger();

        for (int i = 1; i <= threadCount; i++) {
            long userId = i;
            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    bookingService.createBooking(new BookingRequest(userId, seat.getId()));
                    successCount.incrementAndGet();
                } catch (SeatNotAvailableException e) {
                    seatTakenCount.incrementAndGet();
                } catch (Exception e) {
                    unexpectedErrorCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();    // "I'm finished"
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();

        boolean allFinished = doneLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(allFinished, "Threads did not finish in time");
        assertEquals(1, successCount.get(), "Exactly one booking must succeed");
        assertEquals(threadCount - 1, seatTakenCount.get(), "All others must be rejected");
        assertEquals(0, unexpectedErrorCount.get(), "No unexpected errors allowed");
        assertEquals(bookingsBefore + 1, bookingRepository.count());
        assertEquals(SeatStatus.BOOKED, seatRepository.findById(seat.getId()).get().getStatus());
    }
}