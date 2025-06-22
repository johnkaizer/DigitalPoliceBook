package com.kca_2sem_project.digitalob.usersmanagement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String username;
    private String password;
    private String idNumber;
    private String specialization;
    private String gender;
    private String phoneNumber;
    private String image;
    private String role;

    // New fields for Digital OB Management
    private String badgeNumber;        // Official police badge/service number
    private String department;         // Criminal, Traffic, Cyber, Narcotics, Special Investigations
    private String policeRank;              // Constable, Corporal, Sergeant, Inspector, etc.

    private LocalDateTime created;
    private LocalDateTime updated;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.created = now;
        this.updated = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }
}