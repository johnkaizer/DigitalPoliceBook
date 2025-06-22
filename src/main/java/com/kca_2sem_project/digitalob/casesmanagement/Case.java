package com.kca_2sem_project.digitalob.casesmanagement;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cases")
public class Case implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reporter Details
    private String reporterName;
    private String reporterPhone;
    private String reporterIdNumber;

    // Recording Officer Details
    private String officerName;
    private String officerBadgeNumber;

    // Crime Details
    private String crimeLocation;
    private LocalDateTime crimeDateTime;

    // Case Information
    private String caseDescription;
    private String caseStatus;        // OPEN, UNDER_INVESTIGATION, CLOSED, etc.
    private String caseType;          // THEFT, ASSAULT, BURGLARY, FRAUD, etc.

    // Timestamps
    private LocalDateTime created;
    private LocalDateTime updated;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.created = now;
        this.updated = now;
        if (this.caseStatus == null) {
            this.caseStatus = "OPEN";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }
}
