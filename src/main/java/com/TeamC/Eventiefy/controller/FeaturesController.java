package com.TeamC.Eventiefy.controller;

import com.TeamC.Eventiefy.entity.Features;
import com.TeamC.Eventiefy.entity.SubscriptionPlan;
import com.TeamC.Eventiefy.services.FeaturesService;
import com.TeamC.Eventiefy.services.SubscriptionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/features")
public class FeaturesController {

    private final FeaturesService featuresService;

    @Autowired
    public FeaturesController(FeaturesService featuresService) {
        this.featuresService = featuresService;
    }

    @PostMapping("/add")
    public ResponseEntity<Features> createFeature(@RequestBody Features feature){
        Features createdFeature=featuresService.createFeature(feature);
        return ResponseEntity.ok(createdFeature);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeature(@PathVariable Long id){
        featuresService.deleteFeature(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
    @PostMapping("/add/{subscriptionPlanId}")
    public ResponseEntity<Features> createFeatureForPlan(
            @PathVariable Long subscriptionPlanId,
            @RequestBody Features feature) {
        try {
            Features createdFeature = featuresService.createFeatureForPlan(feature, subscriptionPlanId);
            return new ResponseEntity<>(createdFeature, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/delete/{featureId}/{subscriptionPlanId}")
    public ResponseEntity<Void> deleteFeatureFromPlan(
            @PathVariable Long featureId,
            @PathVariable Long subscriptionPlanId) {
        try {
            featuresService.deleteFeatureFromPlan(featureId, subscriptionPlanId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
