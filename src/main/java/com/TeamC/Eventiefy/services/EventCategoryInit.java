package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.EventCategory;

import java.util.List;

public interface EventCategoryInit {
    List<EventCategory> getAllCategories();
    EventCategory getCategoryById(Long id);
    EventCategory createCategory(EventCategory category);
    void deleteCategory(Long id);
}
