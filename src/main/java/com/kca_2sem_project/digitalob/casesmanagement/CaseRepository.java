package com.kca_2sem_project.digitalob.casesmanagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {

    // Find cases by status
    List<Case> findByCaseStatus(String caseStatus);

    // Find cases by officer badge number
    List<Case> findByOfficerBadgeNumber(String officerBadgeNumber);

    // Find cases by reporter ID number
    List<Case> findByReporterIdNumber(String reporterIdNumber);

    // Find cases by case type
    List<Case> findByCaseType(String caseType);

    // Find cases by location containing text (case insensitive)
    @Query("SELECT c FROM Case c WHERE LOWER(c.crimeLocation) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Case> findByCrimeLocationContaining(@Param("location") String location);
}
