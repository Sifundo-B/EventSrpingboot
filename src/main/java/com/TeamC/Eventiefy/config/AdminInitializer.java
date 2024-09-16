package com.TeamC.Eventiefy.config;

import com.TeamC.Eventiefy.entity.Features;
import com.TeamC.Eventiefy.entity.SubscriptionPlan;
import com.TeamC.Eventiefy.enums.Role;
import com.TeamC.Eventiefy.repository.FeaturesRepo;
import com.TeamC.Eventiefy.repository.SubscriptionPlanRepo;
import com.TeamC.Eventiefy.repository.UserRepo;
import com.TeamC.Eventiefy.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private FeaturesRepo featuresRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubscriptionPlanRepo subscriptionPlanRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize subscription plans if none exist
        if (subscriptionPlanRepo.count() == 0) {
            SubscriptionPlan familyPlan = SubscriptionPlan.builder()
                    .planName("Family Plan")
                    .price(new BigDecimal("90"))
                    .durationInMonths(1)
                    .build();

            SubscriptionPlan individualPlan = SubscriptionPlan.builder()
                    .planName("Individual Plan")
                    .price(new BigDecimal("40"))
                    .durationInMonths(1)
                    .build();

            // Save subscription plans
            subscriptionPlanRepo.saveAll(List.of(familyPlan, individualPlan));

            // Create features and associate them with subscription plans
            Features familyPlanFeature1 = Features.builder()
                    .Name("Bulk ticket discounts")
                    .Description("The attendee can get discounts on bulk tickets")
                    .subscriptionPlan(familyPlan)
                    .build();

            Features familyPlanFeature2 = Features.builder()
                    .Name("Immediate Access")
                    .Description("The attendee will have access to posted events immediately when they are posted")
                    .subscriptionPlan(familyPlan)
                    .build();

            Features familyPlanFeature3 = Features.builder()
                    .Name("Flash Run")
                    .Description("The attendee will stand a chance to win bulk tickets during flash run")
                    .subscriptionPlan(familyPlan)
                    .build();

            Features individualPlanFeature1 = Features.builder()
                    .Name("Ticket discounts")
                    .Description("The attendee can get discounts on a ticket")
                    .subscriptionPlan(individualPlan)
                    .build();

            Features individualPlanFeature2 = Features.builder()
                    .Name("Immediate Access")
                    .Description("The attendee will have access to posted events immediately when they are posted")
                    .subscriptionPlan(individualPlan)
                    .build();

            Features individualPlanFeature3 = Features.builder()
                    .Name("Flash Run")
                    .Description("The attendee will stand a chance to win bulk tickets during flash run")
                    .subscriptionPlan(individualPlan)
                    .build();

            // Save features
            featuresRepo.saveAll(List.of(
                    familyPlanFeature1,
                    familyPlanFeature2,
                    familyPlanFeature3,
                    individualPlanFeature1,
                    individualPlanFeature2,
                    individualPlanFeature3
            ));
        }

        // Initialize default admin user if not present
        if (userRepo.findByEmail("busi@eventiefy.com").isEmpty()) {
            User admin = User.builder()
                    .firstName("Busisiwe")
                    .lastName("Eventiefy")
                    .email("busi@eventiefy.com")
                    .password(passwordEncoder.encode("admin123"))
                    .address("123 Admin Street")
                    .phoneNumber("1234567890")
                    .role(Role.Admin)
                    .build();
            userRepo.save(admin);
        }
    }
}
