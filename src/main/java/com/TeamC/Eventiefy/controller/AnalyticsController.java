package com.TeamC.Eventiefy.controller;

import com.TeamC.Eventiefy.entity.Ticket;
import com.TeamC.Eventiefy.services.SalesAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final SalesAnalyticsService salesAnalyticsService;

    @GetMapping("/sales")
    public ResponseEntity<?> getSalesAnalytics(
            @RequestParam Long organizerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        if (startDate == null || endDate == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "title", "Bad Request",
                    "message", "Start date and end date are required"
            ));
        }

        Map<String, Object> analytics = salesAnalyticsService.getSalesAnalytics(organizerId, startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/ticket-history/{userId}")
    public ResponseEntity<?> getUserTicketHistory(@PathVariable Long userId) {
        List<Ticket> ticketHistory = salesAnalyticsService.getUserTicketHistory(userId);
        return ResponseEntity.ok(ticketHistory);
    }
}
