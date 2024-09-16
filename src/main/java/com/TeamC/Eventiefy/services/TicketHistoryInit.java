package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.TicketHistory;

import java.util.List;

public interface TicketHistoryInit {

    // Method to create a new ticket history record
    TicketHistory createTicket(TicketHistory ticketHistory);

    // Method to get all ticket history records
    List<TicketHistory> getAllTickets();

    // Method to find ticket history records by user ID
    List<TicketHistory> findTicketsByUserId(Long id);

    // Method to find a ticket history record by its ID
    TicketHistory findTicketById(Long id);
}
