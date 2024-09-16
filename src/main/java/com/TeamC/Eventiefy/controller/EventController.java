package com.TeamC.Eventiefy.controller;

import com.TeamC.Eventiefy.entity.Event;
import com.TeamC.Eventiefy.enums.Status;
import com.TeamC.Eventiefy.services.EventServiceImpl;
import com.TeamC.Eventiefy.services.NotificationServiceImpl;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventServiceImpl eventServiceImpl;
    private final Cloudinary cloudinary;

    @Autowired
    NotificationServiceImpl notificationService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestParam("event") String eventJson,
                                             @RequestParam("organizerId") Long organizerId,
                                             @RequestParam("image") MultipartFile image) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Event event = mapper.readValue(eventJson, Event.class);

        // Upload image to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
        event.setImageUrl((String) uploadResult.get("url"));

        Event createdEvent = eventServiceImpl.createEvent(event, organizerId);
        return ResponseEntity.ok(createdEvent);
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventServiceImpl.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventServiceImpl.getEventById(id);
        return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event updatedEvent) {
        Event event = eventServiceImpl.updateEvent(id, updatedEvent);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventServiceImpl.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status")
    public ResponseEntity<List<Event>> getEventsByStatus(@RequestParam Status status) {
        List<Event> events = eventServiceImpl.getEventsByStatus(status);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<Event>> getEventsByStatuses(@RequestParam List<Status> statuses) {
        List<Event> events = eventServiceImpl.getEventsByStatuses(statuses);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/submitted")
    public ResponseEntity<List<Event>> getSubmittedEvents() {
        List<Event> events = eventServiceImpl.getSubmittedEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Event>> getEventsByCategories(@RequestParam List<Long> categories) {
        List<Event> events = eventServiceImpl.getEventsByCategories(categories);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Event>> createMultipleEvents(@RequestBody List<Event> events) {
        List<Event> createdEvents = eventServiceImpl.createMultipleEvents(events);
        return ResponseEntity.ok(createdEvents);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Event> approveEvent(@PathVariable Long id, @RequestParam Long organizerId) {
        try {
            Optional<Event> eventOpt = eventServiceImpl.getEventById(id);
            if (eventOpt.isPresent()) {
                Event event = eventOpt.get();
                event.setStatus(Status.APPROVED);
                Event updatedEvent = eventServiceImpl.saveEvent(event);

                // Safeguard the notification process
                try {
                    notificationService.notifyOrganizerEventApproved(organizerId, event);
                } catch (Exception e) {
                    System.out.println("Notification failed: " + e.getMessage());
                }

                return ResponseEntity.ok(updatedEvent);
            } else {
                System.out.println("Event with ID " + id + " not found.");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("Error during event approval: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
    @PutMapping("/{id}/reject")
    public ResponseEntity<Event> rejectEvent(@PathVariable Long id, @RequestParam Long organizerId, @RequestBody String rejectionComment) {
        try {
            Optional<Event> eventOpt = eventServiceImpl.getEventById(id);
            if (eventOpt.isPresent()) {
                Event event = eventOpt.get();
                // Reject event and update status
                event.setStatus(Status.REJECTED);
                Event updatedEvent = eventServiceImpl.saveEvent(event);

                try {
                    // Notify the organizer with rejection comment
                    notificationService.notifyOrganizerEventRejected(organizerId, event, rejectionComment);
                } catch (Exception e) {
                    // Handle the error, maybe log it or return an error response if critical
                    System.out.println("Notification failed: " + e.getMessage());
                }

                return ResponseEntity.ok(updatedEvent);
            } else {
                System.out.println("Event with ID " + id + " not found.");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/searchByName")
    public ResponseEntity<List<Event>> searchEventsByName(@RequestParam String name) {
        List<Event> events = eventServiceImpl.searchEventsByName(name);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/searchByCategoryName")
    public ResponseEntity<List<Event>> searchEventsByCategoryName(@RequestParam String categoryName) {
        List<Event> events = eventServiceImpl.searchEventsByCategoryName(categoryName);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> getEventNameSuggestions(@RequestParam String partialName) {
        List<String> suggestions = eventServiceImpl.getEventNameSuggestions(partialName);
        return ResponseEntity.ok(suggestions);
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<Event> purchaseTicket(@PathVariable Long id) {
        Event event = eventServiceImpl.purchaseTicket(id);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{eventId}/deduct-ticket")
    public ResponseEntity<Map<String, String>> deductTicket(@PathVariable Long eventId) {
        try {
            eventServiceImpl.deductTicket(eventId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Ticket deducted successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error deducting ticket: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
