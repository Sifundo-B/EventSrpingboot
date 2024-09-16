package com.TeamC.Eventiefy.controller;

import com.TeamC.Eventiefy.entity.TicketHistory;
import com.TeamC.Eventiefy.services.TicketHistoryServiceImpl;
import com.TeamC.Eventiefy.services.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ticketHistory")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class TicketHistoryController {

    // Autowire the UserServiceImpl to manage user-related operations
    @Autowired
    private UserServiceImpl userService;

    // Autowire the TicketHistoryServiceImpl to manage ticket history-related operations
    @Autowired
    private TicketHistoryServiceImpl ticketService;

    // Endpoint to create a new ticket history record
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TicketHistory> createTicket(@RequestBody TicketHistory history) {
        try {
            // Create a new ticket history record
            TicketHistory ticketHistory = ticketService.createTicket(history);
            // Return the created ticket history with HTTP status 201 Created
            return new ResponseEntity<>(ticketHistory, HttpStatus.CREATED);
        } catch (Exception e) {
            // Return HTTP status 500 Internal Server Error if an exception occurs
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to get all ticket history records
    @GetMapping
    public List<TicketHistory> getAllTickets() {
        // Return a list of all ticket history records
        return ticketService.getAllTickets();
    }

    // Endpoint to get a specific ticket history record by its ID
    @GetMapping("/{id}")
    public ResponseEntity<TicketHistory> getTicketById(@PathVariable Long id) {
        try {
            // Find the ticket history record by its ID
            TicketHistory ticketHistory = ticketService.findTicketById(id);
            // Return the ticket history record with HTTP status 200 OK
            return new ResponseEntity<>(ticketHistory, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            // Return HTTP status 404 Not Found if the ticket history record is not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Return HTTP status 500 Internal Server Error if an exception occurs
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to get all ticket history records for a specific user by user ID
    @GetMapping("/tickets/user/{id}")
    public ResponseEntity<List<TicketHistory>> getTicketsByUserId(@PathVariable Long id) {
        // Find all ticket history records by user ID
        List<TicketHistory> tickets = ticketService.findTicketsByUserId(id);
        if (tickets.isEmpty()) {
            // Return HTTP status 404 Not Found if no ticket history records are found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Return the list of ticket history records with HTTP status 200 OK
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }
}
