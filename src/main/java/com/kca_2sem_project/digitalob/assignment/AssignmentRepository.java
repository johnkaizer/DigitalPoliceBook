package com.kca_2sem_project.digitalob.assignment;;
import com.kca_2sem_project.digitalob.casesmanagement.Case;
import com.kca_2sem_project.digitalob.usersmanagement.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    // Find assignments by case
    List<Assignment> findByAssignedCase(Case assignedCase);

    // Find assignments by case ID
    List<Assignment> findByAssignedCaseId(Long caseId);

    // Find assignments by officer
    List<Assignment> findByAssignedOfficer(User assignedOfficer);

    // Find assignments by officer ID
    List<Assignment> findByAssignedOfficerId(Long officerId);

    // Find assignments by officer badge number
    @Query("SELECT a FROM Assignment a WHERE a.assignedOfficer.badgeNumber = :badgeNumber")
    List<Assignment> findByOfficerBadgeNumber(@Param("badgeNumber") String badgeNumber);

    // Find assignments by status
    List<Assignment> findByAssignmentStatus(String assignmentStatus);

    // Find assignments by priority
    List<Assignment> findByPriority(String priority);

    // Find assignments by admin who assigned
    List<Assignment> findByAssignedBy(User assignedBy);

    // Find assignments by admin ID
    List<Assignment> findByAssignedById(Long assignedById);

    // Find assignments due before a certain date
    List<Assignment> findByDueDateBefore(LocalDateTime dueDate);

    // Find overdue assignments (due date passed and not completed)
    @Query("SELECT a FROM Assignment a WHERE a.dueDate < :currentDate AND a.assignmentStatus NOT IN ('COMPLETED', 'REASSIGNED')")
    List<Assignment> findOverdueAssignments(@Param("currentDate") LocalDateTime currentDate);

    // Find active assignments for an officer
    @Query("SELECT a FROM Assignment a WHERE a.assignedOfficer.id = :officerId AND a.assignmentStatus IN ('ASSIGNED', 'IN_PROGRESS')")
    List<Assignment> findActiveAssignmentsByOfficer(@Param("officerId") Long officerId);

    // Find completed assignments for an officer
    @Query("SELECT a FROM Assignment a WHERE a.assignedOfficer.id = :officerId AND a.assignmentStatus = 'COMPLETED'")
    List<Assignment> findCompletedAssignmentsByOfficer(@Param("officerId") Long officerId);

    // Check if a case is already assigned to an officer
    @Query("SELECT a FROM Assignment a WHERE a.assignedCase.id = :caseId AND a.assignedOfficer.id = :officerId AND a.assignmentStatus NOT IN ('COMPLETED', 'REASSIGNED')")
    Optional<Assignment> findActiveCaseAssignmentToOfficer(@Param("caseId") Long caseId, @Param("officerId") Long officerId);

    // Find assignments by case type through case relationship
    @Query("SELECT a FROM Assignment a WHERE a.assignedCase.caseType = :caseType")
    List<Assignment> findByCaseType(@Param("caseType") String caseType);

    // Find assignments by date range
    @Query("SELECT a FROM Assignment a WHERE a.assignmentDate BETWEEN :startDate AND :endDate")
    List<Assignment> findByAssignmentDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Count assignments by status
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.assignmentStatus = :status")
    long countByAssignmentStatus(@Param("status") String status);

    // Find assignments by officer department
    @Query("SELECT a FROM Assignment a WHERE a.assignedOfficer.department = :department")
    List<Assignment> findByOfficerDepartment(@Param("department") String department);
}
