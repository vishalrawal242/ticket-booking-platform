// Package = the folder this class lives in. Keeps the project organised.
package com.vishal.ticketbooking.entity;

// Imports the JPA annotations (@Entity, @Id, @Column, etc.).
// The * means "import everything from jakarta.persistence".
import jakarta.persistence.*;
// Java's date + time type, used for the event's start time.
import java.time.LocalDateTime;

// @Entity tells Hibernate: "this class represents a database table".
@Entity
// @Table sets the table name in PostgreSQL to "events".
// Without it, Hibernate would use the class name "Event".
@Table(name = "events")
public class Event {

    // @Id marks this field as the primary key (unique ID of each row).
    @Id
    // The database auto-generates the ID (1, 2, 3...) so we never set it manually.
    // IDENTITY = PostgreSQL's auto-increment column.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable = false -> the column can't be empty (NOT NULL in SQL).
    @Column(nullable = false)
    private String name;

    // Where the event happens. Also required.
    @Column(nullable = false)
    private String venue;

    // name = "event_time" -> the DB column is event_time (snake_case),
    // while the Java field is eventTime (camelCase). Also required.
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    // Empty constructor: Hibernate REQUIRES this to create objects
    // when it reads rows from the database.
    public Event() {
    }

    // Convenience constructor so we can create an Event in one line in our code.
    // No id here because the database generates it.
    public Event(String name, String venue, LocalDateTime eventTime) {
        this.name = name;
        this.venue = venue;
        this.eventTime = eventTime;
    }

    // Getter for id. No setter, because the database controls the id.
    public Long getId() { return id; }

    // Getter + setter: read and change the event name.
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Getter + setter for the venue.
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    // Getter + setter for the event time.
    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
}