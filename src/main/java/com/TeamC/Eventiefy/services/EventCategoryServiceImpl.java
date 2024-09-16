package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.EventCategory;
import com.TeamC.Eventiefy.repository.EventCategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventCategoryServiceImpl implements EventCategoryInit {
    @Autowired
    private EventCategoryRepo eventCategoryRepo;

    @Override
    public List<EventCategory> getAllCategories() {
        return eventCategoryRepo.findAll();
    }

    @Override
    public EventCategory getCategoryById(Long id) {
        return eventCategoryRepo.findById(id).orElse(null);
    }

    @Override
    public EventCategory createCategory(EventCategory category) {
        return eventCategoryRepo.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        eventCategoryRepo.deleteById(id);
    }
}
