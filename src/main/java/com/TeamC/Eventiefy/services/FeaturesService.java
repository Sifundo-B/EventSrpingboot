package com.TeamC.Eventiefy.services;

import com.TeamC.Eventiefy.entity.Features;
import com.TeamC.Eventiefy.entity.SubscriptionPlan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface FeaturesService {
    void deleteFeatureFromPlan(Long featureId, Long subscriptionPlanId);
    Features createFeature(Features feature);
    void deleteFeature(Long id);
    Features createFeatureForPlan(Features feature, Long subscriptionPlanId);
}