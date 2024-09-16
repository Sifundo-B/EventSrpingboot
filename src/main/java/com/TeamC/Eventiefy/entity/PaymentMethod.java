package com.TeamC.Eventiefy.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payment_methods")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generated ID
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Column(nullable = false, unique = true) // Name must be unique and not null
    private String name;

    @NotBlank(message = "Description is mandatory")
    @Column(nullable = false) // Description must not be null
    private String description;

    // Default constructor
    public PaymentMethod() {
    }

    // Constructor with parameters
    public PaymentMethod(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
