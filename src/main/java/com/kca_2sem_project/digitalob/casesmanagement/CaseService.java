package com.kca_2sem_project.digitalob.casesmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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
}