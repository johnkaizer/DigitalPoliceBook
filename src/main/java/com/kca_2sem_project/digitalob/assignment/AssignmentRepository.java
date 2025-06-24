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

    // Find all assignments with relationships loaded
    @Query("SELECT a FROM Assignment a " +
            "LEFT JOIN FETCH a.assignedCase " +
            "LEFT JOIN FETCH a.assignedOfficer " +
            "LEFT JOIN FETCH a.assignedBy " +
            "ORDER BY a.created DESC")
    List<Assignment> findAllWithRelationships();

    // Find assignment by ID with relationships loaded
    @Query("SELECT a FROM Assignment a " +
            "LEFT JOIN FETCH a.assignedCase " +
            "LEFT JOIN FETCH a.assignedOfficer " +
            "LEFT JOIN FETCH a.assignedBy " +
            "WHERE a.id = :id")
    Optional<Assignment> findByIdWithRelationships(@Param("id") Long id);

    // Find assignments by case ID with relationships loaded
    @Query("SELECT a FROM Assignment a " +
            "LEFT JOIN FETCH a.assignedCase " +
            "LEFT JOIN FETCH a.assignedOfficer " +
            "LEFT JOIN FETCH a.assignedBy " +
            "WHERE a.assignedCase.id = :caseId " +
            "ORDER BY a.created DESC")
    List<Assignment> findByCaseIdWithRelationships(@Param("caseId") Long caseId);

    // Find assignments by officer ID with relationships loaded
    @Query("SELECT a FROM Assignment a " +
            "LEFT JOIN FETCH a.assignedCase " +
            "LEFT JOIN FETCH a.assignedOfficer " +
            "LEFT JOIN FETCH a.assignedBy " +
            "WHERE a.assignedOfficer.id = :officerId " +
            "ORDER BY a.created DESC")
    List<Assignment> findByOfficerIdWithRelationships(@Param("officerId") Long officerId);

    // Find assignments by officer badge with relationships loaded
    @Query("SELECT a FROM Assignment a " +
            "LEFT JOIN FETCH a.assignedCase " +
            "LEFT JOIN FETCH a.assignedOfficer " +
            "LEFT JOIN FETCH a.assignedBy " +
            "WHERE a.assignedOfficer.badgeNumber = :badgeNumber " +
            "ORDER BY a.created DESC")
    List<Assignment> findByOfficerBadgeWithRelationships(@Param("badgeNumber") String badgeNumber);

    // Find assignments by status with relationships loaded
    @Query("SELECT a FROM Assignment a " +
            "LEFT JOIN FETCH a.assignedCase " +
            "LEFT JOIN FETCH a.assignedOfficer " +
            "LEFT JOIN FETCH a.assignedBy " +
            "WHERE a.assignmentStatus = :status " +
            "ORDER BY a.created DESC")
    List<Assignment> findByStatusWithRelationships(@Param("status") String status);

    // Find assignments by priority with relationships loaded
    @Query("SELECT a FROM Assignment a " +
            "LEFT JOIN FETCH a.assignedCase " +
            "LEFT JOIN FETCH a.assignedOfficer " +
            "LEFT JOIN FETCH a.assignedBy " +
            "WHERE a.priority = :priority " +
            "ORDER BY a.created DESC")
    List<Assignment> findByPriorityWithRelationships(@Param("priority") String priority);

    // Find active assignments by officer ID with relationships loaded
    @Query("SELECT a FROM Assignment a " +
            "LEFT JOIN FETCH a.assignedCase " +
            "LEFT JOIN FETCH a.assignedOfficer " +
            "LEFT JOIN FETCH a.assignedBy " +
            "WHERE a.assignedOfficer.id = :officerId " +
            "AND a.assignmentStatus IN ('ASSIGNED', 'IN_PROGRESS') " +
            "ORDER BY a.created DESC")
    List<Assignment> findActiveByOfficerIdWithRelationships(@Param("officerId") Long officerId);

    // Find completed assignments by officer ID with relationships loaded
    @Query("SELECT a FROM Assignment a " +
            "LEFT JOIN FETCH a.assignedCase " +
            "LEFT JOIN FETCH a.assignedOfficer " +
            "LEFT JOIN FETCH a.assignedBy " +
            "WHERE a.assignedOfficer.id = :officerId " +
            "AND a.assignmentStatus = 'COMPLETED' " +
            "ORDER BY a.completedDate DESC")
    List<Assignment> findCompletedByOfficerIdWithRelationships(@Param("officerId") Long officerId);

    // Find overdue assignments with relationships loaded
    @Query("SELECT a FROM Assignment a " +
            "LEFT JOIN FETCH a.assignedCase " +
            "LEFT JOIN FETCH a.assignedOfficer " +
            "LEFT JOIN FETCH a.assignedBy " +
            "WHERE a.dueDate < CURRENT_TIMESTAMP " +
            "AND a.assignmentStatus != 'COMPLETED' " +
            "ORDER BY a.dueDate ASC")
    List<Assignment> findOverdueWithRelationships();

    // Find assignments by department with relationships loaded
    @Query("SELECT a FROM Assignment a " +
            "LEFT JOIN FETCH a.assignedCase " +
            "LEFT JOIN FETCH a.assignedOfficer " +
            "LEFT JOIN FETCH a.assignedBy " +
            "WHERE a.assignedOfficer.department = :department " +
            "ORDER BY a.created DESC")
    List<Assignment> findByDepartmentWithRelationships(@Param("department") String department);

    // Find assignments by case type with relationships loaded
    @Query("SELECT a FROM Assignment a " +
            "LEFT JOIN FETCH a.assignedCase " +
            "LEFT JOIN FETCH a.assignedOfficer " +
            "LEFT JOIN FETCH a.assignedBy " +
            "WHERE a.assignedCase.caseType = :caseType " +
            "ORDER BY a.created DESC")
    List<Assignment> findByCaseTypeWithRelationships(@Param("caseType") String caseType);

    // Count assignments by status
    long countByAssignmentStatus(String assignmentStatus);

    // Additional useful queries
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.assignedOfficer.id = :officerId AND a.assignmentStatus IN ('ASSIGNED', 'IN_PROGRESS')")
    long countActiveAssignmentsByOfficer(@Param("officerId") Long officerId);

    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.dueDate < CURRENT_TIMESTAMP AND a.assignmentStatus != 'COMPLETED'")
    long countOverdueAssignments();
}