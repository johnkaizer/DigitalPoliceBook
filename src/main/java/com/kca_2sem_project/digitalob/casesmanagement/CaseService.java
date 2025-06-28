package com.kca_2sem_project.digitalob.casesmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CaseService {

    @Autowired
    private CaseRepository caseRepository;

    // Create a new case
    public Case createCase(Case caseEntity) {
        // Ensure status is set to OPEN by default
        if (caseEntity.getCaseStatus() == null || caseEntity.getCaseStatus().isEmpty()) {
            caseEntity.setCaseStatus("OPEN");
        }
        return caseRepository.save(caseEntity);
    }

    // Get all cases
    public List<Case> getAllCases() {
        return caseRepository.findAll();
    }

    // Get case by ID
    public Optional<Case> getCaseById(Long id) {
        return caseRepository.findById(id);
    }

    // Update case
    public Case updateCase(Long id, Case caseDetails) {
        Optional<Case> existingCase = caseRepository.findById(id);
        if (existingCase.isPresent()) {
            Case caseToUpdate = existingCase.get();

            // Update fields
            caseToUpdate.setReporterName(caseDetails.getReporterName());
            caseToUpdate.setReporterPhone(caseDetails.getReporterPhone());
            caseToUpdate.setReporterIdNumber(caseDetails.getReporterIdNumber());
            caseToUpdate.setOfficerName(caseDetails.getOfficerName());
            caseToUpdate.setOfficerBadgeNumber(caseDetails.getOfficerBadgeNumber());
            caseToUpdate.setCrimeLocation(caseDetails.getCrimeLocation());
            caseToUpdate.setCrimeDateTime(caseDetails.getCrimeDateTime());
            caseToUpdate.setCaseDescription(caseDetails.getCaseDescription());
            caseToUpdate.setCaseStatus(caseDetails.getCaseStatus());
            caseToUpdate.setCaseType(caseDetails.getCaseType());

            return caseRepository.save(caseToUpdate);
        }
        return null;
    }

    // Delete case
    public boolean deleteCase(Long id) {
        if (caseRepository.existsById(id)) {
            caseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Get cases by status
    public List<Case> getCasesByStatus(String status) {
        return caseRepository.findByCaseStatus(status);
    }

    // Get cases by officer badge number
    public List<Case> getCasesByOfficer(String badgeNumber) {
        return caseRepository.findByOfficerBadgeNumber(badgeNumber);
    }

    // Get cases by reporter ID
    public List<Case> getCasesByReporter(String reporterIdNumber) {
        return caseRepository.findByReporterIdNumber(reporterIdNumber);
    }

    // Get cases by type
    public List<Case> getCasesByType(String caseType) {
        return caseRepository.findByCaseType(caseType);
    }

    // Get cases by location
    public List<Case> getCasesByLocation(String location) {
        return caseRepository.findByCrimeLocationContaining(location);
    }

    // Update case status only
    public Case updateCaseStatus(Long id, String status) {
        Optional<Case> existingCase = caseRepository.findById(id);
        if (existingCase.isPresent()) {
            Case caseToUpdate = existingCase.get();
            caseToUpdate.setCaseStatus(status);
            return caseRepository.save(caseToUpdate);
        }
        return null;
    }

    // Add this method to your existing CaseService class

    public List<CaseController.CaseLocationMapping> getCasesLocationMapping() {
        List<Case> allCases = caseRepository.findAll();

        // Group cases by location
        Map<String, List<Case>> locationGroups = allCases.stream()
                .filter(c -> c.getCrimeLocation() != null && !c.getCrimeLocation().trim().isEmpty())
                .collect(Collectors.groupingBy(Case::getCrimeLocation));

        List<CaseController.CaseLocationMapping> mappings = new ArrayList<>();

        for (Map.Entry<String, List<Case>> entry : locationGroups.entrySet()) {
            String location = entry.getKey();
            List<Case> locationCases = entry.getValue();

            // Count total cases for this location
            Long caseCount = (long) locationCases.size();

            // Group by case type within this location
            Map<String, Long> caseTypeCounts = locationCases.stream()
                    .filter(c -> c.getCaseType() != null)
                    .collect(Collectors.groupingBy(
                            Case::getCaseType,
                            Collectors.counting()
                    ));

            // Convert to CaseTypeSummary list
            List<CaseController.CaseTypeSummary> caseTypes = caseTypeCounts.entrySet().stream()
                    .map(typeEntry -> new CaseController.CaseTypeSummary(typeEntry.getKey(), typeEntry.getValue()))
                    .sorted((a, b) -> Long.compare(b.getCount(), a.getCount())) // Sort by count descending
                    .collect(Collectors.toList());

            // Find most recent case date for this location
            String mostRecentDate = locationCases.stream()
                    .filter(c -> c.getCreated() != null)
                    .max(Comparator.comparing(Case::getCreated))
                    .map(c -> c.getCreated().toString())
                    .orElse("N/A");

            mappings.add(new CaseController.CaseLocationMapping(location, caseCount, caseTypes, mostRecentDate));
        }

        // Sort by case count descending
        mappings.sort((a, b) -> Long.compare(b.getCaseCount(), a.getCaseCount()));

        return mappings;
    }
    // Add these methods to your existing CaseService class

    public Long getOpenCasesCount() {
        return caseRepository.countByCaseStatusIn(Arrays.asList("OPEN", "UNDER_INVESTIGATION"));
    }

    public Long getResolvedCasesCount() {
        return caseRepository.countByCaseStatusIn(Arrays.asList("CLOSED", "RESOLVED"));
    }

    public Long getUniqueLocationsCount() {
        return caseRepository.countDistinctByCrimeLocationIsNotNull();
    }

    public Long getUniqueOfficersCount() {
        return caseRepository.countDistinctByOfficerBadgeNumberIsNotNull();
    }

    public List<DashboardController.RecentActivity> getRecentCaseActivity(int limit) {
        List<Case> recentCases = caseRepository.findTop10ByOrderByUpdatedDesc();

        return recentCases.stream()
                .limit(limit)
                .map(this::convertToRecentActivity)
                .collect(Collectors.toList());
    }

    private DashboardController.RecentActivity convertToRecentActivity(Case caseEntity) {
        String activityType;
        String icon;
        String iconColor;
        String description;

        // Determine activity type based on case status and creation/update time
        if (caseEntity.getCreated().equals(caseEntity.getUpdated())) {
            activityType = "CASE_CREATED";
            icon = "fas fa-plus-circle";
            iconColor = "#3498db";
            description = String.format("New %s case created at %s",
                    caseEntity.getCaseType(), caseEntity.getCrimeLocation());
        } else {
            activityType = "CASE_UPDATED";
            icon = getIconForCaseStatus(caseEntity.getCaseStatus());
            iconColor = getColorForCaseStatus(caseEntity.getCaseStatus());
            description = String.format("Case #%d updated - Status: %s",
                    caseEntity.getId(), caseEntity.getCaseStatus());
        }

        // Format timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        String timestamp = caseEntity.getUpdated().format(formatter);

        return new DashboardController.RecentActivity(
                activityType,
                description,
                timestamp,
                icon,
                iconColor
        );
    }

    private String getIconForCaseStatus(String status) {
        switch (status.toUpperCase()) {
            case "OPEN":
                return "fas fa-folder-open";
            case "UNDER_INVESTIGATION":
                return "fas fa-search";
            case "CLOSED":
            case "RESOLVED":
                return "fas fa-check-circle";
            default:
                return "fas fa-file";
        }
    }

    private String getColorForCaseStatus(String status) {
        switch (status.toUpperCase()) {
            case "OPEN":
                return "#e74c3c";
            case "UNDER_INVESTIGATION":
                return "#f39c12";
            case "CLOSED":
            case "RESOLVED":
                return "#27ae60";
            default:
                return "#95a5a6";
        }
    }
}