package com.TeamC.Eventiefy.controller;

import com.TeamC.Eventiefy.entity.EventCategory;
import com.TeamC.Eventiefy.services.EventCategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/eventCategories")
@RequiredArgsConstructor
public class EventCategoryController {
    private final EventCategoryServiceImpl eventCategoryService;

    @GetMapping
    public ResponseEntity<List<EventCategory>> getAllCategories() {
        List<EventCategory> categories = eventCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventCategory> getCategoryById(@PathVariable Long id) {
        EventCategory eventCategory = eventCategoryService.getCategoryById(id);
        return ResponseEntity.ok(eventCategory);
    }

    @PostMapping
    public ResponseEntity<EventCategory> createCategory(@RequestBody EventCategory category) {
        EventCategory createdCategory = eventCategoryService.createCategory(category);
        return ResponseEntity.ok(createdCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        eventCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
