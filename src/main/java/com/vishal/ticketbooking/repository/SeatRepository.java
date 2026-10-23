package com.vishal.ticketbooking.repository;

import com.vishal.ticketbooking.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    // Spring reads the method name and builds the SQL itself:
    // SELECT * FROM seats WHERE event_id = ?
    // Used by the endpoint that shows all seats of an event.
    List<Seat> findByEventId(Long eventId);

    // PESSIMISTIC_WRITE = PostgreSQL runs "SELECT ... FOR UPDATE".
    // The selected seat row is LOCKED until the current transaction ends.
    // Any other request trying to lock the same seat must WAIT its turn,
    // so two users can never book the same seat at the same time.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // Custom query (JPQL) that selects one seat by its id.
    @Query("SELECT s FROM Seat s WHERE s.id = :seatId")
    // Optional = the seat may not exist, so the result can be empty.
    // @Param links :seatId in the query to the method argument.
    Optional<Seat> findByIdForUpdate(@Param("seatId") Long seatId);
}