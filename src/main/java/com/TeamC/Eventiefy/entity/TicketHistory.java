package com.TeamC.Eventiefy.entity;

import com.TeamC.Eventiefy.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Data
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String eventName; // Name of the event associated with the ticket

    private String image; // Image URL or path for the event

    private String attendeeName; // Name of the attendee who purchased the ticket

    private String ticketNumber; // Unique ticket number or identifier

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate; // Date when the ticket was purchased

    private double price; // Price of the ticket

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate; // Date of the event

    private String location; // Location of the event

    private String ticketType; // Type or category of the ticket (e.g., VIP, Regular)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference // Prevents infinite recursion in JSON serialization
    private User user; // User who purchased the ticket
    private String qrCodeUrl;
    private String cloudinaryUrl; // URL of the ticket PDF saved on Cloudinary
}

