package com.TeamC.Eventiefy.controller;

import com.TeamC.Eventiefy.entity.TicketHistory;
import com.TeamC.Eventiefy.repository.TicketHistoryRepo;
import com.TeamC.Eventiefy.services.TicketService;
import com.TeamC.Eventiefy.services.UserServiceImpl;
import com.TeamC.Eventiefy.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    @Autowired
    private TicketHistoryRepo ticketHistoryRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private TicketService ticketService;

    @GetMapping("/history")
    public ResponseEntity<List<TicketHistory>> getTicketHistory() {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).build(); // Unauthorized if no user is found
        }
        List<TicketHistory> history = ticketHistoryRepository.findByUserId(user.getId());
        if (history.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no tickets are found
        }
        return ResponseEntity.ok(history);
    }

    @GetMapping("/download/{ticketId}")
    public ResponseEntity<?> downloadTicket(@PathVariable Long ticketId) {
        System.out.println("Downloading ticket with ID: " + ticketId);
        try {
            TicketHistory ticket = ticketHistoryRepository.findById(ticketId)
                    .orElseThrow(() -> new RuntimeException("Ticket not found for ID: " + ticketId));

            String url;
            if (ticket.getCloudinaryUrl() != null && !ticket.getCloudinaryUrl().isEmpty()) {
                url = ticket.getCloudinaryUrl(); // Use the public Cloudinary URL directly
            } else {
                url = ticketService.generateAndUploadTicketPDF(ticket);
            }

            System.out.println("Generated URL: " + url);
            return ResponseEntity.status(302)
                    .header(HttpHeaders.LOCATION, url)
                    .build();

        } catch (RuntimeException e) {
            System.err.println("Runtime Exception: " + e.getMessage());
            return ResponseEntity.status(500).body("Runtime Exception: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O Exception: " + e.getMessage());
            return ResponseEntity.status(500).body("I/O Exception: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error generating URL: " + e.getMessage());
            return ResponseEntity.status(500).body("Error generating URL: " + e.getMessage());
        }
    }



}