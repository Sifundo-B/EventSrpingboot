package com.TeamC.Eventiefy.entity;

import com.TeamC.Eventiefy.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscriptions")

public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String planName;
    private Integer  durationInMonths;
    private BigDecimal price;
    private LocalDate subscriptionExpiryDate;

    // One subscription plan can be associated with many users
    @OneToMany(mappedBy = "subscriptionPlan")
    @JsonManagedReference
    private Set<User> users;

    @OneToMany(mappedBy = "subscriptionPlan")
    private Set<Features> features;

    public void addFeature(Features feature) {
        features.add(feature);
        feature.setSubscriptionPlan(this);
    }

    // Method to remove a feature from this subscription plan
    public void removeFeature(Features feature) {
        features.remove(feature);
        feature.setSubscriptionPlan(null);

    }}



