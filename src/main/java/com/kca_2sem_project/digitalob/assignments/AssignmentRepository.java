package com.kca_2sem_project.digitalob.assignments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByOfficerId(String officerId);
    List<Assignment> findByPoliceCase_Id(Long caseId);
    List<Assignment> findByStatus(Assignment.AssignmentStatus status);
    List<Assignment> findByOfficerSpecialization(String specialization);
    List<Assignment> findByOfficerIdAndStatus(String officerId, Assignment.AssignmentStatus status);
}

