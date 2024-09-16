package com.TeamC.Eventiefy.repository;

import com.TeamC.Eventiefy.entity.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TicketHistoryRepo extends JpaRepository<TicketHistory, Long> {

    // Custom query method to find all ticket history records by user ID
    List<TicketHistory> findByUserId(Long userId);
}
