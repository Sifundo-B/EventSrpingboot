package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.Event;
import com.TeamC.Eventiefy.entity.EventCategory;
import com.TeamC.Eventiefy.enums.Status;
import com.TeamC.Eventiefy.repository.EventRepository;
import com.TeamC.Eventiefy.repository.EventCategoryRepo;
import com.TeamC.Eventiefy.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    @Autowired
    private final EventRepository eventRepository;
    private final UserServiceImpl userService;
    private final NotificationServiceImpl notificationService;

    @Autowired(required = false)
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private final EventCategoryRepo eventCategoryRepo;

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public Event createEvent(Event event, Long organizerId) {
        // Ensure the event id is not set manually
        event.setId(null);
        // Fetch the organizer User entity
        User organizer = userService.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found with id " + organizerId));

        event.setOrganizer(organizer);  // Set the organizer
        event.setUserId(organizer.getId()); // Set the user ID
        event.setSubmissionDate(LocalDateTime.now());
        event.setStatus(Status.SUBMITTED);
        Event savedEvent = eventRepository.save(event);

        // Trigger the notification for event creation
        notificationService.notifyAdminsOnNewEvent(savedEvent);

        // Check if redisTemplate is available and set event in Redis if it is
        if (redisTemplate != null) {
            redisTemplate.opsForValue().set("EVENT_" + savedEvent.getId(), savedEvent);
        }

        return savedEvent;
    }

    @Override
    public Event updateEvent(Long id, Event updatedEvent) {
        return eventRepository.findById(id).map(event -> {
            event.setName(updatedEvent.getName());
            event.setDescription(updatedEvent.getDescription());
            event.setDate(updatedEvent.getDate());
            event.setEventCategory(updatedEvent.getEventCategory());
            event.setImageUrl(updatedEvent.getImageUrl());
            event.setLatitude(updatedEvent.getLatitude());
            event.setLongitude(updatedEvent.getLongitude());
            event.setAddress(updatedEvent.getAddress());
            event.setStatus(updatedEvent.getStatus());
            event.setSubmissionDate(LocalDateTime.now());
            event.setNumberOfTickets(updatedEvent.getNumberOfTickets());
            return eventRepository.save(event);
        }).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @Override
    public List<Event> getEventsByStatus(Status status) {
        return eventRepository.findByStatus(status);
    }

    @Override
    public List<Event> getEventsByStatuses(List<Status> statuses) {
        return eventRepository.findByStatusIn(statuses);
    }

    @Override
    public List<Event> getSubmittedEvents() {
        return eventRepository.findByStatus(Status.SUBMITTED);
    }

    @Override
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id " + id));
        eventRepository.delete(event);
    }

    @Override
    public List<Event> getEventsByCategories(List<Long> categoryIds) {
        List<EventCategory> categories = eventCategoryRepo.findAllById(categoryIds);
        return eventRepository.findByEventCategoryIn(categories);
    }

    @Override
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event rejectEvent(Long id, Long organizerId, String rejectionComment) {
        return eventRepository.findById(id).map(event -> {
            event.setStatus(Status.REJECTED);
            event.setRejectionComment(rejectionComment);
            Event updatedEvent = eventRepository.save(event);
            notificationService.notifyOrganizerEventRejected(organizerId, updatedEvent, rejectionComment);
            return updatedEvent;
        }).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public List<Event> searchEventsByName(String name) {
        return eventRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Event> searchEventsByCategory(Long categoryId) {
        EventCategory category = eventCategoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return eventRepository.findByEventCategory(category);
    }

    public List<Event> searchEventsByCategoryName(String categoryName) {
        return eventRepository.findByCategoryName(categoryName);
    }

    public List<String> getEventNameSuggestions(String partialName) {
        return eventRepository.findByNameContainingIgnoreCase(partialName)
                .stream()
                .map(Event::getName)
                .distinct()
                .limit(10) // Limit to 10 suggestions
                .toList();
    }

    @Override
    @Transactional
    public Event purchaseTicket(Long eventId) {
        synchronized (this) {
            return eventRepository.findById(eventId).map(event -> {
                if (event.getNumberOfTickets() > 0) {
                    event.setNumberOfTickets(event.getNumberOfTickets() - 1);
                    return eventRepository.save(event);
                } else {
                    throw new RuntimeException("No tickets available");
                }
            }).orElseThrow(() -> new RuntimeException("Event not found"));
        }
    }

    @Override
    public void deductTicket(Long eventId) throws Exception {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            if (event.getNumberOfTickets() > 0) {
                event.setNumberOfTickets(event.getNumberOfTickets() - 1);
                eventRepository.save(event);
            } else {
                throw new RuntimeException("No tickets available");
            }
        } else {
            throw new RuntimeException("Event not found");
        }
    }

    @Override
    public List<Event> createMultipleEvents(List<Event> events) {
        events.forEach(event -> {
            event.setSubmissionDate(LocalDateTime.now());
            event.setStatus(Status.SUBMITTED);
        });
        return eventRepository.saveAll(events);
    }
}
