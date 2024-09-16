package com.TeamC.Eventiefy.entity;

import com.TeamC.Eventiefy.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "features")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Features {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String Name;
    String Description;

    @ManyToOne
    @JoinColumn(name = "subscription_plan_id")
    @JsonManagedReference
    @JsonIgnore
    private SubscriptionPlan subscriptionPlan;

}
