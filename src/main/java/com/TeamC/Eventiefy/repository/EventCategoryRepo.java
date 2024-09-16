package com.TeamC.Eventiefy.repository;

import com.TeamC.Eventiefy.entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventCategoryRepo extends JpaRepository<EventCategory, Long> {
    Optional<EventCategory> findByName(String name);
}
