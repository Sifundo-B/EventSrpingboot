package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.Event;
import com.TeamC.Eventiefy.enums.Status;

import java.util.List;
import java.util.Optional;

public interface EventService {
    List<Event> getAllEvents();

    Optional<Event> getEventById(Long id);

    Event createEvent(Event event, Long organizerId);

    Event updateEvent(Long id, Event eventDetails);

    void deleteEvent(Long id);

    List<Event> getEventsByCategories(List<Long> eventCategories_id);

    List<Event> getEventsByStatus(Status status);

    List<Event> getEventsByStatuses(List<Status> statuses);

    List<Event> getSubmittedEvents();

    void deductTicket(Long eventId) throws Exception;

    Event saveEvent(Event event);

    Event purchaseTicket(Long eventId);

    List<Event> createMultipleEvents(List<Event> events);
}
