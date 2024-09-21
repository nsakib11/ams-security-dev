package com.ams.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 45)
    private String email;

    @Column(length = 40)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 64)
    private String passwordHash;

    @Column(name = "mobile_no", length = 16)
    private String mobileNo;

    @Column(nullable = false, length = 16)
    private String role;

    @Column(nullable = false, length = 16)
    private String status;

    @Column(name = "registered_on", nullable = false)
    private LocalDateTime registeredOn;

    @Column(name = "approved_on")
    private LocalDateTime approvedOn;

    @Column(name = "rejected_on")
    private LocalDateTime rejectedOn;

    @Column(name = "rejection_cause", length = 100)
    private String rejectionCause;

}
