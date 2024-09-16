package com.TeamC.Eventiefy.entity;

import com.TeamC.Eventiefy.enums.NotificationType;
import com.TeamC.Eventiefy.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate timestamp;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @JsonBackReference("event-notifications")
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "user_id_fk"))
    @JsonBackReference("user-notifications")
    private User recipient;
    private String rejectionComment; // New field for storing rejection comments
    private boolean isRead;
}