package com.TeamC.Eventiefy.repository;

import com.TeamC.Eventiefy.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepo extends JpaRepository<SubscriptionPlan,Long> {
    @Query("SELECT sp FROM SubscriptionPlan sp JOIN sp.users u WHERE u.id = :userId")
    Optional<SubscriptionPlan> findByUserId(@Param("userId") Long userId);
}
