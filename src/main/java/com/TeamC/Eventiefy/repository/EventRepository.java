package com.TeamC.Eventiefy.repository;

import com.TeamC.Eventiefy.entity.Event;
import com.TeamC.Eventiefy.entity.EventCategory;
import com.TeamC.Eventiefy.enums.Status;
import com.TeamC.Eventiefy.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEventCategoryIn(List<EventCategory> categories);
    List<Event> findByStatus(Status status);
    List<Event> findByStatusIn(List<Status> statuses);
    List<Event> findByNameContainingIgnoreCase(String name);
    List<Event> findByEventCategory(EventCategory category);
    @Query("SELECT e FROM Event e WHERE e.eventCategory.name = :categoryName")
    List<Event> findByCategoryName(String categoryName);

//    @Query("SELECT e FROM Event e WHERE e.organizer.id = :organizerId AND e.date BETWEEN :startDate AND :endDate")
//    List<Event> findByOrganizerIdAndDateBetween(Long organizerId, LocalDate startDate, LocalDate endDate);
@Query("SELECT e FROM Event e WHERE e.organizer.id = :organizerId AND e.date BETWEEN :startDate AND :endDate")
List<Event> findByOrganizerIdAndDateBetween(Long organizerId, LocalDateTime startDate, LocalDateTime endDate);

    Optional<Event> findByName(String eventName);
}
