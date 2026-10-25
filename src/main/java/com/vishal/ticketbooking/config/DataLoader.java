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

@Component
public class DataLoader implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    public DataLoader(EventRepository eventRepository, SeatRepository seatRepository) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public void run(String... args) {

        if (eventRepository.count() > 0) {
            return;
        }

        Event event = eventRepository.save(
                new Event("Flash Sale Concert", "Pune Arena", LocalDateTime.now().plusDays(30)));

        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            seats.add(new Seat(event, "A" + i));
        }
        seatRepository.saveAll(seats);
    }
}