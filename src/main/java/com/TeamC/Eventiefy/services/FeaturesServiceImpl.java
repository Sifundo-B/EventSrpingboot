package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.Features;
import com.TeamC.Eventiefy.entity.SubscriptionPlan;
import com.TeamC.Eventiefy.repository.FeaturesRepo;
import com.TeamC.Eventiefy.repository.SubscriptionPlanRepo;
import com.TeamC.Eventiefy.repository.UserRepo;
import com.TeamC.Eventiefy.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeaturesServiceImpl implements FeaturesService {

    private final FeaturesRepo featuresRepo;
    private final UserRepo userRepo;
    private final SubscriptionPlanRepo subscriptionPlanRepo;
    @Autowired
    public FeaturesServiceImpl(FeaturesRepo featuresRepo, UserRepo userRepo,SubscriptionPlanRepo subscriptionPlanRepo) {
        this.featuresRepo = featuresRepo;
        this.userRepo = userRepo;
        this.subscriptionPlanRepo=subscriptionPlanRepo;
    }

    public Features createFeature(Features feature){
        return featuresRepo.save(feature);
    }
    public void deleteFeature(Long id){
        this.featuresRepo.deleteById(id);
    }
    public Features createFeatureForPlan(Features feature, Long subscriptionPlanId) {
        // Retrieve the subscription plan by its ID
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepo.findById(subscriptionPlanId)
                .orElseThrow(() -> new RuntimeException("Subscription Plan not found"));

        // Set the subscription plan for the feature
        feature.setSubscriptionPlan(subscriptionPlan);

        // Save the feature
        featuresRepo.save(feature);

        // Return the created feature
        return feature;
    }
    public void deleteFeatureFromPlan(Long featureId, Long subscriptionPlanId) {
        // Retrieve the subscription plan by its ID
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepo.findById(subscriptionPlanId)
                .orElseThrow(() -> new RuntimeException("Subscription Plan not found"));

        // Find the feature to be deleted
        Features feature = featuresRepo.findById(featureId)
                .orElseThrow(() -> new RuntimeException("Feature not found"));

        // Remove the feature from the subscription plan
        subscriptionPlan.removeFeature(feature);

        // Save the updated subscription plan
        subscriptionPlanRepo.save(subscriptionPlan);

        // Optionally, delete the feature if needed
        // featureRepo.delete(feature);
    }}
