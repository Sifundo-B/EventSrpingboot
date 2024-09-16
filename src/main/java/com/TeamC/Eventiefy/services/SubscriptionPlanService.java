

package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.Event;
import com.TeamC.Eventiefy.entity.SubscriptionPlan;
import com.TeamC.Eventiefy.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanService {
    SubscriptionPlan createPlanForUser(SubscriptionPlan existingPlan, User user);
    SubscriptionPlan createPlan(SubscriptionPlan subscriptionPlan);
    SubscriptionPlan getUserPlan();
    SubscriptionPlan updateUserSubscriptionPlan(Long newPlanId);
    void deleteUserSubscriptionPlan(User user);
    void deletePlan(Long id);
    SubscriptionPlan getPlanById(Long id);
     List<SubscriptionPlan> getAllSubscriptions();
}
