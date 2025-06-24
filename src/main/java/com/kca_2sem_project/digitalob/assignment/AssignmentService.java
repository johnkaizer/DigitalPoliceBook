package com.kca_2sem_project.digitalob.assignment;

import com.kca_2sem_project.digitalob.casesmanagement.Case;
import com.kca_2sem_project.digitalob.casesmanagement.CaseRepository;
import com.kca_2sem_project.digitalob.usersmanagement.User;
import com.kca_2sem_project.digitalob.usersmanagement.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all assignments with relationships loaded
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAllWithRelationships();
    }

    // Get assignment by ID with relationships loaded
    public Optional<Assignment> getAssignmentById(Long id) {
        return assignmentRepository.findByIdWithRelationships(id);
    }

    // Create assignment
    public Assignment createAssignment(Long caseId, Long officerId, Long assignedById,
                                       String priority, String assignmentNotes, LocalDateTime dueDate) {

        Case assignedCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Case not found with ID: " + caseId));

        User assignedOfficer = userRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Officer not found with ID: " + officerId));

        User assignedBy = userRepository.findById(assignedById)
                .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + assignedById));

        Assignment assignment = new Assignment();
        assignment.setAssignedCase(assignedCase);
        assignment.setAssignedOfficer(assignedOfficer);
        assignment.setAssignedBy(assignedBy);
        assignment.setPriority(priority);
        assignment.setAssignmentNotes(assignmentNotes);
        assignment.setDueDate(dueDate);
        assignment.setAssignmentStatus("ASSIGNED");

        return assignmentRepository.save(assignment);
    }

    // Get assignments by case ID
    public List<Assignment> getAssignmentsByCaseId(Long caseId) {
        return assignmentRepository.findByCaseIdWithRelationships(caseId);
    }

    // Get assignments by officer ID
    public List<Assignment> getAssignmentsByOfficerId(Long officerId) {
        return assignmentRepository.findByOfficerIdWithRelationships(officerId);
    }

    // Get assignments by officer badge
    public List<Assignment> getAssignmentsByOfficerBadge(String badgeNumber) {
        return assignmentRepository.findByOfficerBadgeWithRelationships(badgeNumber);
    }

    // Get assignments by status
    public List<Assignment> getAssignmentsByStatus(String status) {
        return assignmentRepository.findByStatusWithRelationships(status);
    }

    // Get assignments by priority
    public List<Assignment> getAssignmentsByPriority(String priority) {
        return assignmentRepository.findByPriorityWithRelationships(priority);
    }

    // Get active assignments for officer
    public List<Assignment> getActiveAssignmentsByOfficer(Long officerId) {
        return assignmentRepository.findActiveByOfficerIdWithRelationships(officerId);
    }

    // Get completed assignments for officer
    public List<Assignment> getCompletedAssignmentsByOfficer(Long officerId) {
        return assignmentRepository.findCompletedByOfficerIdWithRelationships(officerId);
    }

    // Get overdue assignments
    public List<Assignment> getOverdueAssignments() {
        return assignmentRepository.findOverdueWithRelationships();
    }

    // Update assignment status
    public Assignment updateAssignmentStatus(Long id, String status) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findByIdWithRelationships(id);
        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();
            assignment.setAssignmentStatus(status);
            if (status.equals("COMPLETED")) {
                assignment.setCompletedDate(LocalDateTime.now());
            }
            return assignmentRepository.save(assignment);
        }
        return null;
    }

    // Update assignment priority
    public Assignment updateAssignmentPriority(Long id, String priority) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findByIdWithRelationships(id);
        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();
            assignment.setPriority(priority);
            return assignmentRepository.save(assignment);
        }
        return null;
    }

    // Update assignment due date
    public Assignment updateAssignmentDueDate(Long id, LocalDateTime dueDate) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findByIdWithRelationships(id);
        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();
            assignment.setDueDate(dueDate);
            return assignmentRepository.save(assignment);
        }
        return null;
    }

    // Update assignment notes
    public Assignment updateAssignmentNotes(Long id, String notes) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findByIdWithRelationships(id);
        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();
            assignment.setAssignmentNotes(notes);
            return assignmentRepository.save(assignment);
        }
        return null;
    }

    // Reassign case
    public Assignment reassignCase(Long assignmentId, Long newOfficerId, Long reassignedById, String reason) {
        Optional<Assignment> existingAssignmentOpt = assignmentRepository.findByIdWithRelationships(assignmentId);
        if (existingAssignmentOpt.isPresent()) {
            Assignment existingAssignment = existingAssignmentOpt.get();

            // Mark existing assignment as reassigned
            existingAssignment.setAssignmentStatus("REASSIGNED");
            existingAssignment.setCompletedDate(LocalDateTime.now());
            assignmentRepository.save(existingAssignment);

            // Create new assignment
            User newOfficer = userRepository.findById(newOfficerId)
                    .orElseThrow(() -> new RuntimeException("Officer not found with ID: " + newOfficerId));

            User reassignedBy = userRepository.findById(reassignedById)
                    .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + reassignedById));

            Assignment newAssignment = new Assignment();
            newAssignment.setAssignedCase(existingAssignment.getAssignedCase());
            newAssignment.setAssignedOfficer(newOfficer);
            newAssignment.setAssignedBy(reassignedBy);
            newAssignment.setPriority(existingAssignment.getPriority());
            newAssignment.setDueDate(existingAssignment.getDueDate());
            newAssignment.setAssignmentNotes(reason);
            newAssignment.setAssignmentStatus("ASSIGNED");

            return assignmentRepository.save(newAssignment);
        }
        return null;
    }

    // Delete assignment
    public boolean deleteAssignment(Long id) {
        if (assignmentRepository.existsById(id)) {
            assignmentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Get assignments by department
    public List<Assignment> getAssignmentsByDepartment(String department) {
        return assignmentRepository.findByDepartmentWithRelationships(department);
    }

    // Get assignments by case type
    public List<Assignment> getAssignmentsByCaseType(String caseType) {
        return assignmentRepository.findByCaseTypeWithRelationships(caseType);
    }

    // Get assignment count by status
    public long getAssignmentCountByStatus(String status) {
        return assignmentRepository.countByAssignmentStatus(status);
    }
}