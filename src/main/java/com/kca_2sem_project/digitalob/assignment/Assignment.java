package com.kca_2sem_project.digitalob.assignment;

import com.kca_2sem_project.digitalob.casesmanagement.Case;
import com.kca_2sem_project.digitalob.usersmanagement.User;
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
@Table(name = "assignments")
public class Assignment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Case Information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case assignedCase;

    // Officer Information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false)
    private User assignedOfficer;

    // Admin who made the assignment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by_id", nullable = false)
    private User assignedBy;

    // Assignment Details
    private String assignmentStatus;     // ASSIGNED, IN_PROGRESS, COMPLETED, REASSIGNED
    private String priority;             // LOW, MEDIUM, HIGH, URGENT
    private String assignmentNotes;      // Additional notes or instructions
    private LocalDateTime assignmentDate;
    private LocalDateTime dueDate;       // Expected completion date
    private LocalDateTime completedDate;

    // Timestamps
    private LocalDateTime created;
    private LocalDateTime updated;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.created = now;
        this.updated = now;
        this.assignmentDate = now;
        if (this.assignmentStatus == null) {
            this.assignmentStatus = "ASSIGNED";
        }
        if (this.priority == null) {
            this.priority = "MEDIUM";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }

    // Helper methods to get case and officer details without full entity loading
    public String getCaseNumber() {
        return assignedCase != null ? assignedCase.getId().toString() : null;
    }

    public String getOfficerBadgeNumber() {
        return assignedOfficer != null ? assignedOfficer.getBadgeNumber() : null;
    }

    public String getOfficerName() {
        return assignedOfficer != null ? assignedOfficer.getFullName() : null;
    }

    public String getAssignedByName() {
        return assignedBy != null ? assignedBy.getFullName() : null;
    }
}
