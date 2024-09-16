package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.TicketHistory;
import com.TeamC.Eventiefy.repository.TicketHistoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketHistoryServiceImpl implements TicketHistoryInit {

    // Autowire the TicketHistoryRepo to interact with the database
    @Autowired
    private TicketHistoryRepo historyRepo;

    // Method to create a new ticket history record
    @Override
    public TicketHistory createTicket(TicketHistory ticketHistory) {
        // Ensure the ID is not set manually here to avoid conflicts
        ticketHistory.setId(null); // Optional: explicitly clear the ID to ensure it's generated
        // Save the ticket history record to the database and return the saved entity
        return historyRepo.save(ticketHistory);
    }

    // Method to get all ticket history records
    @Override
    public List<TicketHistory> getAllTickets() {
        // Retrieve and return a list of all ticket history records from the database
        return historyRepo.findAll();
    }

    // Method to find ticket history records by user ID
    @Override
    public List<TicketHistory> findTicketsByUserId(Long id) {
        // Retrieve and return a list of ticket history records for a specific user ID
        return historyRepo.findByUserId(id);
    }

    // Method to find a ticket history record by its ID
    @Override
    public TicketHistory findTicketById(Long id) {
        // Retrieve an optional ticket history record by its ID
        Optional<TicketHistory> ticket = historyRepo.findById(id);
        // Return the ticket history record if found, otherwise return null
        return ticket.orElse(null);
    }
}
