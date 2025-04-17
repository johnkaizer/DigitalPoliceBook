package com.kca_2sem_project.digitalob.assignments;

import com.kca_2sem_project.digitalob.cases.Case;
import com.kca_2sem_project.digitalob.cases.CaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CaseRepository caseRepository;

    @Autowired
    public AssignmentService(AssignmentRepository assignmentRepository, CaseRepository caseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.caseRepository = caseRepository;
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    public Optional<Assignment> getAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }

    public List<Assignment> getAssignmentsByOfficerId(String officerId) {
        return assignmentRepository.findByOfficerId(officerId);
    }

    public List<Assignment> getAssignmentsByCaseId(Long caseId) {
        return assignmentRepository.findByPoliceCase_Id(caseId);
    }

    public List<Assignment> getAssignmentsByStatus(Assignment.AssignmentStatus status) {
        return assignmentRepository.findByStatus(status);
    }

    public List<Assignment> getAssignmentsBySpecialization(String specialization) {
        return assignmentRepository.findByOfficerSpecialization(specialization);
    }

    public Assignment createAssignment(Long caseId, Assignment assignment) {
        Optional<Case> optionalCase = caseRepository.findById(caseId);
        if (optionalCase.isEmpty()) {
            throw new RuntimeException("Case not found with id: " + caseId);
        }

        assignment.setPoliceCase(optionalCase.get());
        assignment.setAssignedDate(LocalDateTime.now());

        // Set default status as PENDING_APPROVAL if not provided
        if (assignment.getStatus() == null) {
            assignment.setStatus(Assignment.AssignmentStatus.PENDING_APPROVAL);
        }

        return assignmentRepository.save(assignment);
    }

    public Assignment updateAssignment(Long id, Assignment assignmentDetails) {
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(id);
        if (optionalAssignment.isEmpty()) {
            throw new RuntimeException("Assignment not found with id: " + id);
        }

        Assignment existingAssignment = optionalAssignment.get();

        // Update fields
        existingAssignment.setOfficerId(assignmentDetails.getOfficerId());
        existingAssignment.setOfficerSpecialization(assignmentDetails.getOfficerSpecialization());
        existingAssignment.setAssignmentNotes(assignmentDetails.getAssignmentNotes());

        // Don't update case, assigned date, or assigned by

        return assignmentRepository.save(existingAssignment);
    }

    public Assignment updateAssignmentStatus(Long id, Assignment.AssignmentStatus status, String approvedBy) {
        Optional<Assignment> optionalAssignment = assignmentRepository.findById(id);
        if (optionalAssignment.isEmpty()) {
            throw new RuntimeException("Assignment not found with id: " + id);
        }

        Assignment existingAssignment = optionalAssignment.get();
        existingAssignment.setStatus(status);

        // Update additional fields based on status
        if (status == Assignment.AssignmentStatus.APPROVED) {
            existingAssignment.setApprovalDate(LocalDateTime.now());
            existingAssignment.setApprovedBy(approvedBy);
        } else if (status == Assignment.AssignmentStatus.COMPLETED) {
            existingAssignment.setCompletionDate(LocalDateTime.now());
        }

        return assignmentRepository.save(existingAssignment);
    }

    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
    }

    // Method to recommend officers for cases based on specialization
    public List<Assignment> recommendAssignments(String specialization) {
        return assignmentRepository.findByOfficerSpecialization(specialization);
    }

    // Method to get officer workload (number of active assignments)
    public long getOfficerWorkload(String officerId) {
        List<Assignment> activeAssignments = assignmentRepository.findByOfficerIdAndStatus(
                officerId, Assignment.AssignmentStatus.APPROVED);
        return activeAssignments.size();
    }
}