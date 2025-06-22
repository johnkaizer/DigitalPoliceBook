package com.kca_2sem_project.digitalob.casesmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cases")
@CrossOrigin(origins = "*")
public class CaseController {

    @Autowired
    private CaseService caseService;

    // Create a new case
    @PostMapping
    public ResponseEntity<Case> createCase(@RequestBody Case caseEntity) {
        try {
            Case savedCase = caseService.createCase(caseEntity);
            return new ResponseEntity<>(savedCase, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all cases
    @GetMapping
    public ResponseEntity<List<Case>> getAllCases() {
        try {
            List<Case> cases = caseService.getAllCases();
            if (cases.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(cases, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get case by ID
    @GetMapping("/{id}")
    public ResponseEntity<Case> getCaseById(@PathVariable("id") Long id) {
        try {
            Optional<Case> caseData = caseService.getCaseById(id);
            if (caseData.isPresent()) {
                return new ResponseEntity<>(caseData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update case
    @PutMapping("/{id}")
    public ResponseEntity<Case> updateCase(@PathVariable("id") Long id, @RequestBody Case caseDetails) {
        try {
            Case updatedCase = caseService.updateCase(id, caseDetails);
            if (updatedCase != null) {
                return new ResponseEntity<>(updatedCase, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete case
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCase(@PathVariable("id") Long id) {
        try {
            boolean deleted = caseService.deleteCase(id);
            if (deleted) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get cases by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Case>> getCasesByStatus(@PathVariable("status") String status) {
        try {
            List<Case> cases = caseService.getCasesByStatus(status);
            return new ResponseEntity<>(cases, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get cases by officer badge number
    @GetMapping("/officer/{badgeNumber}")
    public ResponseEntity<List<Case>> getCasesByOfficer(@PathVariable("badgeNumber") String badgeNumber) {
        try {
            List<Case> cases = caseService.getCasesByOfficer(badgeNumber);
            return new ResponseEntity<>(cases, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get cases by reporter ID
    @GetMapping("/reporter/{reporterIdNumber}")
    public ResponseEntity<List<Case>> getCasesByReporter(@PathVariable("reporterIdNumber") String reporterIdNumber) {
        try {
            List<Case> cases = caseService.getCasesByReporter(reporterIdNumber);
            return new ResponseEntity<>(cases, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get cases by type
    @GetMapping("/type/{caseType}")
    public ResponseEntity<List<Case>> getCasesByType(@PathVariable("caseType") String caseType) {
        try {
            List<Case> cases = caseService.getCasesByType(caseType);
            return new ResponseEntity<>(cases, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get cases by location
    @GetMapping("/location/{location}")
    public ResponseEntity<List<Case>> getCasesByLocation(@PathVariable("location") String location) {
        try {
            List<Case> cases = caseService.getCasesByLocation(location);
            return new ResponseEntity<>(cases, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update case status only
    @PatchMapping("/{id}/status")
    public ResponseEntity<Case> updateCaseStatus(@PathVariable("id") Long id, @RequestParam String status) {
        try {
            Case updatedCase = caseService.updateCaseStatus(id, status);
            if (updatedCase != null) {
                return new ResponseEntity<>(updatedCase, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
