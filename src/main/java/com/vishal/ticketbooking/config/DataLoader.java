package com.vishal.ticketbooking.config;

import com.vishal.ticketbooking.entity.Event;
import com.vishal.ticketbooking.entity.Seat;
import com.vishal.ticketbooking.repository.EventRepository;
import com.vishal.ticketbooking.repository.SeatRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// @Component = Spring creates this class automatically at startup.
// CommandLineRunner = its run() method executes once, right after the app starts.
@Component
public class DataLoader implements CommandLineRunner {

    // The repositories we use to save data.
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    // Constructor injection: Spring passes the repositories in for us.
    public DataLoader(EventRepository eventRepository, SeatRepository seatRepository) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public void run(String... args) {
        // If events already exist, do nothing. This stops duplicate data
        // from being inserted every time we restart the app.
        if (eventRepository.count() > 0) {
            return;
        }

        // Save one sample event (30 days from now).
        Event event = eventRepository.save(
                new Event("Flash Sale Concert", "Pune Arena", LocalDateTime.now().plusDays(30)));

        // Build 10 seats: A1, A2 ... A10, all AVAILABLE by default.
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            seats.add(new Seat(event, "A" + i));
        }

        // Save all 10 seats to the database in one go.
        seatRepository.saveAll(seats);
    }
}