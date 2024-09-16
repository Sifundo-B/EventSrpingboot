package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.Features;
import com.TeamC.Eventiefy.entity.SubscriptionPlan;
import com.TeamC.Eventiefy.repository.FeaturesRepo;
import com.TeamC.Eventiefy.repository.SubscriptionPlanRepo;
import com.TeamC.Eventiefy.repository.UserRepo;
import com.TeamC.Eventiefy.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepo subscriptionPlanRepo;
    private final UserRepo userRepo;
    private final FeaturesRepo featuresRepo;

    @Autowired
    public SubscriptionPlanServiceImpl(SubscriptionPlanRepo subscriptionPlanRepo, UserRepo userRepo,FeaturesRepo featuresRepo) {
        this.subscriptionPlanRepo = subscriptionPlanRepo;
        this.userRepo = userRepo;
        this.featuresRepo=featuresRepo;
    }

    public SubscriptionPlan createPlan(SubscriptionPlan subscriptionPlan){
        return subscriptionPlanRepo.save(subscriptionPlan);
    }
    public void deletePlan(Long id){
        this.subscriptionPlanRepo.deleteById(id);
    }

    @Override
    public SubscriptionPlan getPlanById(Long id) {
       return subscriptionPlanRepo.findById(id).orElseThrow(null);
    }

    @Override
    public List<SubscriptionPlan> getAllSubscriptions() {
        return subscriptionPlanRepo.findAll();
    }

    ;

    public SubscriptionPlan createPlanForUser(SubscriptionPlan existingPlan, User user) {
        user.setSubscriptionPlan(existingPlan);

        // Calculate expiration date: 30 days from now
        LocalDate currentDate = LocalDate.now();
        LocalDate subscriptionExpiryDate = currentDate.plus(Period.ofDays(30));

        // Set the expiration date to the existing plan
        existingPlan.setSubscriptionExpiryDate(subscriptionExpiryDate);

        // Save the updated subscription plan
        subscriptionPlanRepo.save(existingPlan);

        // Save the updated user to the repository
        userRepo.save(user);

        // Return the assigned plan
        return existingPlan;
    }
    public SubscriptionPlan getUserPlan() {
        // Get the username from the security context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch the user by username (email)
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get the user's subscription plan
        return user.getSubscriptionPlan();
    }
    public SubscriptionPlan updateUserSubscriptionPlan(Long newPlanId) {
        // Get the username from the security context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Fetch the user by username (email)
      User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Retrieve the new subscription plan by its ID
        SubscriptionPlan newPlan = subscriptionPlanRepo.findById(newPlanId)
                .orElseThrow(() -> new RuntimeException("Subscription Plan not found"));

        // Update the user's subscription plan
        user.setSubscriptionPlan(newPlan);
        userRepo.save(user);

        // Return the updated subscription plan
        return newPlan;
    }
    public void deleteUserSubscriptionPlan(User user) {
        // Set the user's subscription plan to null
        user.setSubscriptionPlan(null);
        userRepo.save(user);
    }

   }