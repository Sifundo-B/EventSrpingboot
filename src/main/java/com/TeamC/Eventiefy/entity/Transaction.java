package com.TeamC.Eventiefy.entity;

import com.TeamC.Eventiefy.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String payerName;
    private String payerEmail;
    private String transactionId;
    private String paymentMethod;
    private Double amount;
    private LocalDateTime transactionDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
