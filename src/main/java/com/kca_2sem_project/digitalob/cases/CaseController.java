package com.kca_2sem_project.digitalob.cases;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;

    @Autowired
    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping
    public ResponseEntity<List<Case>> getAllCases() {
        List<Case> cases = caseService.getAllCases();
        return new ResponseEntity<>(cases, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Case> getCaseById(@PathVariable Long id) {
        Optional<Case> policeCase = caseService.getCaseById(id);
        return policeCase.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/number/{caseNumber}")
    public ResponseEntity<Case> getCaseByCaseNumber(@PathVariable String caseNumber) {
        Case policeCase = caseService.getCaseByCaseNumber(caseNumber);
        if (policeCase != null) {
            return new ResponseEntity<>(policeCase, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/officer/{officerId}")
    public ResponseEntity<List<Case>> getCasesByOfficerId(@PathVariable String officerId) {
        List<Case> cases = caseService.getCasesByOfficerId(officerId);
        return new ResponseEntity<>(cases, HttpStatus.OK);
    }

    @GetMapping("/reporter/{reporterIdNumber}")
    public ResponseEntity<List<Case>> getCasesByReporterIdNumber(@PathVariable String reporterIdNumber) {
        List<Case> cases = caseService.getCasesByReporterIdNumber(reporterIdNumber);
        return new ResponseEntity<>(cases, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Case>> getCasesByStatus(@PathVariable Case.CaseStatus status) {
        List<Case> cases = caseService.getCasesByStatus(status);
        return new ResponseEntity<>(cases, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Case> createCase(@RequestBody Case policeCase) {
        Case newCase = caseService.createCase(policeCase);
        return new ResponseEntity<>(newCase, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Case> updateCase(@PathVariable Long id, @RequestBody Case caseDetails) {
        try {
            Case updatedCase = caseService.updateCase(id, caseDetails);
            return new ResponseEntity<>(updatedCase, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Case> updateCaseStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest statusUpdate) {
        try {
            Case updatedCase = caseService.updateCaseStatus(id, statusUpdate.getStatus());
            return new ResponseEntity<>(updatedCase, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCase(@PathVariable Long id) {
        caseService.deleteCase(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Status update request DTO
    public static class StatusUpdateRequest {
        private Case.CaseStatus status;

        public Case.CaseStatus getStatus() {
            return status;
        }

        public void setStatus(Case.CaseStatus status) {
            this.status = status;
        }
    }
}