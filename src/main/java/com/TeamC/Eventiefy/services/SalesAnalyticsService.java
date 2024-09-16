package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.Event;
import com.TeamC.Eventiefy.entity.Ticket;
import com.TeamC.Eventiefy.enums.TicketStatus;
import com.TeamC.Eventiefy.repository.EventRepository;
import com.TeamC.Eventiefy.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SalesAnalyticsService {
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public Map<String, Object> getSalesAnalytics(Long organizerId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Event> events = eventRepository.findByOrganizerIdAndDateBetween(organizerId, startDate, endDate);

        int totalSales = 0;
        Map<String, Integer> salesByEvent = new HashMap<>();
        Map<String, Integer> ticketsRemainingByEvent = new HashMap<>();

        for (Event event : events) {
            List<Ticket> ticketsSold = ticketRepository.findByEventIdAndStatus(event.getId(), TicketStatus.SOLD);
            List<Ticket> ticketsRemaining = ticketRepository.findByEventIdAndStatus(event.getId(), TicketStatus.AVAILABLE);

            int ticketsSoldCount = ticketsSold.size();
            int ticketsRemainingCount = ticketsRemaining.size();

            salesByEvent.put(event.getName(), ticketsSoldCount);
            ticketsRemainingByEvent.put(event.getName(), ticketsRemainingCount);

            totalSales += ticketsSoldCount;
        }

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalSales", totalSales);
        analytics.put("salesByEvent", salesByEvent);
        analytics.put("ticketsRemainingByEvent", ticketsRemainingByEvent);

        return analytics;
    }
    public List<Ticket> getUserTicketHistory(Long userId) {
        return ticketRepository.findByUserId(userId);
    }
}
