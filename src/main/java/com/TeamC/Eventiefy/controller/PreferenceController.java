package com.TeamC.Eventiefy.controller;

import com.TeamC.Eventiefy.entity.Event;
import com.TeamC.Eventiefy.entity.Preference;
import com.TeamC.Eventiefy.services.EventServiceImpl;
import com.TeamC.Eventiefy.services.PreferenceService;
import com.TeamC.Eventiefy.user.User;
import com.TeamC.Eventiefy.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/preferences")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;
    private final UserRepo userRepo;
    private final EventServiceImpl eventService;

    @GetMapping
    public ResponseEntity<List<Preference>> getUserPreferences(Principal principal) {
        User user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(preferenceService.getUserPreferences(user));
    }

    @PostMapping
    public ResponseEntity<List<Preference>> savePreferences(@RequestBody List<Long> categoryIds, Principal principal) {
        User user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Preference> updatedPreferences = preferenceService.updateUserPreferences(user, categoryIds);

        return ResponseEntity.ok(updatedPreferences);
    }

    @GetMapping("/events")
    public ResponseEntity<List<Event>> getEventsByPreferences(Principal principal) {
        User user = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Long> preferredCategoryIds = preferenceService.getUserPreferredCategoryIds(user);
        List<Event> events = eventService.getEventsByCategories(preferredCategoryIds);
        return ResponseEntity.ok(events);
    }
}
