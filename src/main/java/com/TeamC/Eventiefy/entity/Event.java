package com.TeamC.Eventiefy.entity;

import com.TeamC.Eventiefy.entity.EventCategory;
import com.TeamC.Eventiefy.entity.Notification;
import com.TeamC.Eventiefy.entity.Ticket;
import com.TeamC.Eventiefy.enums.Status;
import com.TeamC.Eventiefy.user.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Event implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "event_category_id")
    private EventCategory eventCategory;

    @Column(length = 2048)
    private String imageUrl;

    private double latitude;
    private double longitude;
    private String address;
    private double price;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String rejectionComment;
    private LocalDateTime submissionDate;
    private int numberOfTickets;
    private int totalTickets;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("event-notifications")
    @JsonIgnore // Ignore notifications to reduce nesting
    private List<Notification> notifications;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Ignore tickets to reduce nesting
    private List<Ticket> tickets;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    @JsonBackReference("user-events")
    private User organizer;

    private Long userId;
}
