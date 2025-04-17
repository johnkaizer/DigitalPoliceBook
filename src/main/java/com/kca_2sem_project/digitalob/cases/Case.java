package com.kca_2sem_project.digitalob.cases;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "police_cases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_number", unique = true)
    private String caseNumber;

    @Column(name = "reporter_name", nullable = false)
    private String reporterName;

    @Column(name = "reporter_phone", nullable = false)
    private String reporterPhone;

    @Column(name = "reporter_id_number", nullable = false)
    private String reporterIdNumber;

    @Column(name = "officer_id", nullable = false)
    private String officerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus status;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private String location;

    // Enum for Case Status
    public enum CaseStatus {
        OPEN, INVESTIGATING, CLOSED, RESOLVED, TRANSFERRED
    }
}
