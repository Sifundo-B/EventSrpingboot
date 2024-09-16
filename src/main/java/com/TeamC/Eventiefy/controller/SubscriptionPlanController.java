package com.TeamC.Eventiefy.controller;

import com.TeamC.Eventiefy.entity.SubscriptionPlan;
import com.TeamC.Eventiefy.repository.SubscriptionPlanRepo;
import com.TeamC.Eventiefy.repository.UserRepo;
import com.TeamC.Eventiefy.services.SubscriptionPlanService;
import com.TeamC.Eventiefy.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;
    private final SubscriptionPlanRepo subscriptionPlanRepo;
    private final UserRepo userRepo;

    @Autowired
    public SubscriptionPlanController(SubscriptionPlanService subscriptionPlanService ,SubscriptionPlanRepo subscriptionPlanRepo,UserRepo userRepo) {
        this.subscriptionPlanService = subscriptionPlanService;
        this.subscriptionPlanRepo=subscriptionPlanRepo;
        this.userRepo=userRepo;
    }
    @PostMapping("/add")
    public ResponseEntity<SubscriptionPlan> createPlan(@RequestBody SubscriptionPlan subscriptionPlan) {
        SubscriptionPlan createdPlan = subscriptionPlanService.createPlan(subscriptionPlan);
        return ResponseEntity.ok(subscriptionPlan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        subscriptionPlanService.deletePlan(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


@GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlan> getPlanById(@PathVariable Long id){
     SubscriptionPlan plan=subscriptionPlanService.getPlanById(id);
      return ResponseEntity.ok(plan);
     }

    @GetMapping("/plan")
    public ResponseEntity<SubscriptionPlan> getUserPlan() {
        try {
            SubscriptionPlan plan = subscriptionPlanService.getUserPlan();
            return new ResponseEntity<>(plan, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


@GetMapping("/all")
    public ResponseEntity<List<SubscriptionPlan>> getAllSubscriptions(){
        List<SubscriptionPlan> subscriptions=subscriptionPlanService.getAllSubscriptions();
        return ResponseEntity.ok(subscriptions);
    }



    @PostMapping("/add/{planId}")
    public ResponseEntity<SubscriptionPlan> createPlanForUser(@PathVariable Long planId) {
        try {
            // Get the logged-in user
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof User) { // Ensure the principal is of type User
                User user = (User) principal;
                String email = user.getUsername();

                User foundUser = userRepo.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // Retrieve the existing subscription plan by its ID
                SubscriptionPlan existingPlan = subscriptionPlanRepo.findById(planId)
                        .orElseThrow(() -> new RuntimeException("Subscription Plan not found"));

                // Assign the existing plan to the user and set the expiry date
                SubscriptionPlan updatedPlan = subscriptionPlanService.createPlanForUser(existingPlan, foundUser);

                return ResponseEntity.ok(updatedPlan);
            } else {
                throw new RuntimeException("Invalid principal");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }


    }
    @PutMapping("/update")
    public ResponseEntity<SubscriptionPlan> updateUserSubscriptionPlan(@RequestParam Long newPlanId) {
        try {
            SubscriptionPlan updatedPlan = subscriptionPlanService.updateUserSubscriptionPlan(newPlanId);
            return new ResponseEntity<>(updatedPlan, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUserSubscriptionPlan() {
        try {
            // Get the username from the security context
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            // Fetch the user by username (email)
            User user = userRepo.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Delete the user's subscription plan
            subscriptionPlanService.deleteUserSubscriptionPlan(user);

            return ResponseEntity.noContent().build(); // Return 204 No Content on success
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}