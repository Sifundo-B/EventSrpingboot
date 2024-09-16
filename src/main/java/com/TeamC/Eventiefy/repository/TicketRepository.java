package com.TeamC.Eventiefy.repository;

import com.TeamC.Eventiefy.entity.Ticket;
import com.TeamC.Eventiefy.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByEventIdAndStatus(Long eventId, TicketStatus status);
    List<Ticket> findByUserId(Long userId); // Add this method
}
