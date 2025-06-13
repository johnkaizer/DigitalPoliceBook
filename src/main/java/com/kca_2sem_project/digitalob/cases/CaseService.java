package com.kca_2sem_project.digitalob.cases;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CaseService {

    private final CaseRepository caseRepository;

    @Autowired
    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public List<Case> getAllCases() {
        return caseRepository.findAll();
    }

    public Optional<Case> getCaseById(Long id) {
        return caseRepository.findById(id);
    }

    public Case getCaseByCaseNumber(String caseNumber) {
        return caseRepository.findByCaseNumber(caseNumber);
    }

    public List<Case> getCasesByOfficerId(String officerId) {
        return caseRepository.findByOfficerId(officerId);
    }

    public List<Case> getCasesByReporterIdNumber(String reporterIdNumber) {
        return caseRepository.findByReporterIdNumber(reporterIdNumber);
    }

    public List<Case> getCasesByStatus(Case.CaseStatus status) {
        return caseRepository.findByStatus(status);
    }

    public Case createCase(Case policeCase) {
        // Generate a unique case number (OB number)
        String caseNumber = "OB" + UUID.randomUUID().toString().substring(0, 3).toUpperCase();
        policeCase.setCaseNumber(caseNumber);

        // Set the current date if not provided
        if (policeCase.getDate() == null) {
            policeCase.setDate(LocalDateTime.now());
        }

        // Set default status if not provided
        if (policeCase.getStatus() == null) {
            policeCase.setStatus(Case.CaseStatus.OPEN);
        }

        return caseRepository.save(policeCase);
    }

    public Case updateCase(Long id, Case caseDetails) {
        Optional<Case> optionalCase = caseRepository.findById(id);
        if (optionalCase.isPresent()) {
            Case existingCase = optionalCase.get();

            // Update fields but maintain the original case number
            existingCase.setReporterName(caseDetails.getReporterName());
            existingCase.setReporterPhone(caseDetails.getReporterPhone());
            existingCase.setReporterIdNumber(caseDetails.getReporterIdNumber());
            existingCase.setOfficerId(caseDetails.getOfficerId());
            existingCase.setStatus(caseDetails.getStatus());
            existingCase.setDescription(caseDetails.getDescription());
            existingCase.setLocation(caseDetails.getLocation());

            return caseRepository.save(existingCase);
        } else {
            throw new RuntimeException("Case not found with id: " + id);
        }
    }

    public void deleteCase(Long id) {
        caseRepository.deleteById(id);
    }

    public Case updateCaseStatus(Long id, Case.CaseStatus status) {
        Optional<Case> optionalCase = caseRepository.findById(id);
        if (optionalCase.isPresent()) {
            Case existingCase = optionalCase.get();
            existingCase.setStatus(status);
            return caseRepository.save(existingCase);
        } else {
            throw new RuntimeException("Case not found with id: " + id);
        }
    }
}
