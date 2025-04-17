package com.kca_2sem_project.digitalob.assignments;
import com.kca_2sem_project.digitalob.cases.Case;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "case_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    private Case policeCase;

    @Column(name = "officer_id", nullable = false)
    private String officerId;

    @Column(name = "officer_specialization")
    private String officerSpecialization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    @Column(name = "assigned_date", nullable = false)
    private LocalDateTime assignedDate;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "assignment_notes")
    private String assignmentNotes;

    @Column(name = "assigned_by", nullable = false)
    private String assignedBy;

    @Column(name = "approved_by")
    private String approvedBy;

    // Enum for Assignment Status
    public enum AssignmentStatus {
        PENDING_APPROVAL, APPROVED, REJECTED, IN_PROGRESS, COMPLETED, REASSIGNED
    }
}
