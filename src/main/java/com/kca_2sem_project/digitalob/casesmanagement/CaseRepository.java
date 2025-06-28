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

    // Count cases by status (for dashboard stats)
    Long countByCaseStatusIn(List<String> statuses);

    // Count distinct locations
    @Query("SELECT COUNT(DISTINCT c.crimeLocation) FROM Case c WHERE c.crimeLocation IS NOT NULL AND c.crimeLocation != ''")
    Long countDistinctByCrimeLocationIsNotNull();

    // Count distinct officers
    @Query("SELECT COUNT(DISTINCT c.officerBadgeNumber) FROM Case c WHERE c.officerBadgeNumber IS NOT NULL AND c.officerBadgeNumber != ''")
    Long countDistinctByOfficerBadgeNumberIsNotNull();

    // Get recent cases for activity feed
    List<Case> findTop10ByOrderByUpdatedDesc();

    // Alternative method using native query if needed
    @Query(value = "SELECT * FROM cases ORDER BY updated DESC LIMIT :limit", nativeQuery = true)
    List<Case> findRecentCases(@Param("limit") int limit);

    // Get cases by status for counting
    List<Case> findByCaseStatusIn(List<String> statuses);

    // Get cases created today (for additional dashboard info if needed)
    @Query("SELECT c FROM Case c WHERE DATE(c.created) = CURRENT_DATE")
    List<Case> findCasesCreatedToday();

    // Get cases updated today
    @Query("SELECT c FROM Case c WHERE DATE(c.updated) = CURRENT_DATE")
    List<Case> findCasesUpdatedToday();
}
