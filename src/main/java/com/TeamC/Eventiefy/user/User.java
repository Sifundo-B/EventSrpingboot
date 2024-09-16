package com.TeamC.Eventiefy.user;

import com.TeamC.Eventiefy.entity.PaymentMethod;
import com.TeamC.Eventiefy.entity.Preference;
import com.TeamC.Eventiefy.entity.SubscriptionPlan;
import com.TeamC.Eventiefy.entity.*;
import com.TeamC.Eventiefy.enums.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}",
            message = "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character")
    @NotBlank(message = "Password is required")
    private String password;

    private String image;

    @Transient
    private String confirmPassword;
    private String resetToken;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "subscription_plan_id", nullable = true)
    @JsonBackReference// Foreign key in user table
    private SubscriptionPlan subscriptionPlan;
    private LocalDate subscriptionExpiryDate;

    @OneToMany(mappedBy = "recipient", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference("user-notifications")
    @JsonIgnore // Ignore notifications to reduce nesting
    private List<Notification> notifications;

    @OneToMany(mappedBy = "organizer", fetch = FetchType.LAZY)
    @JsonManagedReference("user-events")
    @JsonIgnore // Ignore notifications to reduce nesting
    private List<Event> events;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    @JsonIgnore // Ignore notifications to reduce nesting
    private List<Preference> preferences;

    @ManyToOne
    @JoinColumn(name = "preferred_payment_method_id")
    private PaymentMethod preferredPaymentMethod;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore // Ignore notifications to reduce nesting
    private List<Ticket> tickets;

    @Setter
    private boolean isPremium;

    public boolean isPremium() {
        return isPremium;
    }

    private String premiumPackageType; // New field for package type
    private Boolean accessGranted = false; // New field for access permissions

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return userId;
    }
}