package com.kca_2sem_project.digitalob.cases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {
    List<Case> findByOfficerId(String officerId);
    List<Case> findByReporterIdNumber(String reporterIdNumber);
    List<Case> findByStatus(Case.CaseStatus status);
    Case findByCaseNumber(String caseNumber);
}

