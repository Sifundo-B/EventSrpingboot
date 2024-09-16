package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.Preference;
import com.TeamC.Eventiefy.entity.EventCategory;
import com.TeamC.Eventiefy.repository.PreferenceRepo;
import com.TeamC.Eventiefy.repository.EventCategoryRepo;
import com.TeamC.Eventiefy.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final PreferenceRepo preferenceRepo;
    private final EventCategoryRepo eventCategoryRepo;

    public List<Preference> getUserPreferences(User user) {
        return preferenceRepo.findByUser(user);
    }

    public List<Preference> updateUserPreferences(User user, List<Long> selectedCategoryIds) {
        // Fetch existing preferences
        List<Preference> existingPreferences = preferenceRepo.findByUser(user);

        // Find preferences to delete
        List<Preference> preferencesToDelete = existingPreferences.stream()
                .filter(preference -> !selectedCategoryIds.contains(preference.getCategory().getId()))
                .collect(Collectors.toList());

        // Delete the unselected preferences
        preferenceRepo.deleteAll(preferencesToDelete);

        // Find new categories to add as preferences
        List<Preference> preferencesToAdd = selectedCategoryIds.stream()
                .filter(categoryId -> existingPreferences.stream().noneMatch(p -> p.getCategory().getId().equals(categoryId)))
                .map(categoryId -> {
                    EventCategory category = eventCategoryRepo.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Category not found"));
                    return new Preference(user, category);
                })
                .collect(Collectors.toList());

        // Save new preferences
        return preferenceRepo.saveAll(preferencesToAdd);
    }

    public List<Long> getUserPreferredCategoryIds(User user) {
        return preferenceRepo.findByUser(user).stream()
                .map(preference -> preference.getCategory().getId())
                .collect(Collectors.toList());
    }
}
