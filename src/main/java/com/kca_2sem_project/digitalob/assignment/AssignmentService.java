package com.kca_2sem_project.digitalob.assignment;

import com.kca_2sem_project.digitalob.casesmanagement.Case;
import com.kca_2sem_project.digitalob.casesmanagement.CaseRepository;
import com.kca_2sem_project.digitalob.usersmanagement.User;
import com.kca_2sem_project.digitalob.usersmanagement.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a new assignment
    public Assignment createAssignment(Long caseId, Long officerId, Long assignedById, String priority,
                                       String assignmentNotes, LocalDateTime dueDate) {

        // Fetch the case
        Optional<Case> caseOpt = caseRepository.findById(caseId);
        if (!caseOpt.isPresent()) {
            throw new RuntimeException("Case not found with ID: " + caseId);
        }

        // Fetch the officer
        Optional<User> officerOpt = userRepository.findById(officerId);
        if (!officerOpt.isPresent()) {
            throw new RuntimeException("Officer not found with ID: " + officerId);
        }

        // Fetch the admin who is making the assignment
        Optional<User> adminOpt = userRepository.findById(assignedById);
        if (!adminOpt.isPresent()) {
            throw new RuntimeException("Admin not found with ID: " + assignedById);
        }

        // Check if case is already assigned to this officer
        Optional<Assignment> existingAssignment = assignmentRepository
                .findActiveCaseAssignmentToOfficer(caseId, officerId);
        if (existingAssignment.isPresent()) {
            throw new RuntimeException("Case is already assigned to this officer");
        }

        Assignment assignment = new Assignment();
        assignment.setAssignedCase(caseOpt.get());
        assignment.setAssignedOfficer(officerOpt.get());
        assignment.setAssignedBy(adminOpt.get());
        assignment.setPriority(priority);
        assignment.setAssignmentNotes(assignmentNotes);
        assignment.setDueDate(dueDate);

        return assignmentRepository.save(assignment);
    }

    // Get all assignments
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    // Get assignment by ID
    public Optional<Assignment> getAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }

    // Get assignments by case ID
    public List<Assignment> getAssignmentsByCaseId(Long caseId) {
        return assignmentRepository.findByAssignedCaseId(caseId);
    }

    // Get assignments by officer ID
    public List<Assignment> getAssignmentsByOfficerId(Long officerId) {
        return assignmentRepository.findByAssignedOfficerId(officerId);
    }

    // Get assignments by officer badge number
    public List<Assignment> getAssignmentsByOfficerBadge(String badgeNumber) {
        return assignmentRepository.findByOfficerBadgeNumber(badgeNumber);
    }

    // Get assignments by status
    public List<Assignment> getAssignmentsByStatus(String status) {
        return assignmentRepository.findByAssignmentStatus(status);
    }

    // Get assignments by priority
    public List<Assignment> getAssignmentsByPriority(String priority) {
        return assignmentRepository.findByPriority(priority);
    }

    // Get active assignments for an officer
    public List<Assignment> getActiveAssignmentsByOfficer(Long officerId) {
        return assignmentRepository.findActiveAssignmentsByOfficer(officerId);
    }

    // Get completed assignments for an officer
    public List<Assignment> getCompletedAssignmentsByOfficer(Long officerId) {
        return assignmentRepository.findCompletedAssignmentsByOfficer(officerId);
    }

    // Get overdue assignments
    public List<Assignment> getOverdueAssignments() {
        return assignmentRepository.findOverdueAssignments(LocalDateTime.now());
    }

    // Update assignment status
    public Assignment updateAssignmentStatus(Long id, String status) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(id);
        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();
            assignment.setAssignmentStatus(status);

            // Set completion date if status is COMPLETED
            if ("COMPLETED".equals(status)) {
                assignment.setCompletedDate(LocalDateTime.now());
            }

            return assignmentRepository.save(assignment);
        }
        return null;
    }

    // Update assignment priority
    public Assignment updateAssignmentPriority(Long id, String priority) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(id);
        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();
            assignment.setPriority(priority);
            return assignmentRepository.save(assignment);
        }
        return null;
    }

    // Update assignment due date
    public Assignment updateAssignmentDueDate(Long id, LocalDateTime dueDate) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(id);
        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();
            assignment.setDueDate(dueDate);
            return assignmentRepository.save(assignment);
        }
        return null;
    }

    // Update assignment notes
    public Assignment updateAssignmentNotes(Long id, String notes) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(id);
        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();
            assignment.setAssignmentNotes(notes);
            return assignmentRepository.save(assignment);
        }
        return null;
    }

    // Reassign case to different officer
    public Assignment reassignCase(Long assignmentId, Long newOfficerId, Long reassignedById, String reason) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);
        Optional<User> newOfficerOpt = userRepository.findById(newOfficerId);
        Optional<User> adminOpt = userRepository.findById(reassignedById);

        if (assignmentOpt.isPresent() && newOfficerOpt.isPresent() && adminOpt.isPresent()) {
            Assignment oldAssignment = assignmentOpt.get();

            // Mark old assignment as reassigned
            oldAssignment.setAssignmentStatus("REASSIGNED");
            oldAssignment.setAssignmentNotes(oldAssignment.getAssignmentNotes() +
                    "\n[REASSIGNED] " + reason);
            assignmentRepository.save(oldAssignment);

            // Create new assignment
            Assignment newAssignment = new Assignment();
            newAssignment.setAssignedCase(oldAssignment.getAssignedCase());
            newAssignment.setAssignedOfficer(newOfficerOpt.get());
            newAssignment.setAssignedBy(adminOpt.get());
            newAssignment.setPriority(oldAssignment.getPriority());
            newAssignment.setAssignmentNotes("[REASSIGNED FROM " +
                    oldAssignment.getOfficerBadgeNumber() + "] " + reason);
            newAssignment.setDueDate(oldAssignment.getDueDate());

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
        return assignmentRepository.findByOfficerDepartment(department);
    }

    // Get assignments by case type
    public List<Assignment> getAssignmentsByCaseType(String caseType) {
        return assignmentRepository.findByCaseType(caseType);
    }

    // Get assignment statistics
    public long getAssignmentCountByStatus(String status) {
        return assignmentRepository.countByAssignmentStatus(status);
    }
}
