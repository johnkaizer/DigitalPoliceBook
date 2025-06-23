package com.kca_2sem_project.digitalob.casesmanagement;

import com.kca_2sem_project.digitalob.auditlogs.LogService;
import com.kca_2sem_project.digitalob.usersmanagement.User;
import jakarta.servlet.http.HttpSession;
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

    @Autowired
    private LogService logService;

    @Autowired
    private HttpSession session;

    // Create a new case
    @PostMapping
    public ResponseEntity<Case> createCase(@RequestBody Case caseEntity) {
        try {
            // Get the logged-in user from session
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            Case savedCase = caseService.createCase(caseEntity);

            // Log the case creation
            logService.logAction(
                    username,
                    "CASE_CREATED",
                    String.format("Case #%d created by %s. Type: %s, Location: %s, Reporter: %s",
                            savedCase.getId(),
                            username,
                            savedCase.getCaseType(),
                            savedCase.getCrimeLocation(),
                            savedCase.getReporterName())
            );

            return new ResponseEntity<>(savedCase, HttpStatus.CREATED);
        } catch (Exception e) {
            // Log the error
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            logService.logAction(
                    username,
                    "CASE_CREATE_ERROR",
                    "Failed to create case: " + e.getMessage()
            );

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all cases
    @GetMapping
    public ResponseEntity<List<Case>> getAllCases() {
        try {
            List<Case> cases = caseService.getAllCases();
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
                // Log case access
                User loggedInUser = (User) session.getAttribute("loggedInUser");
                String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

                logService.logAction(
                        username,
                        "CASE_VIEWED",
                        String.format("Case #%d viewed by %s", id, username)
                );

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
            // Get the logged-in user from session
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            // Get the existing case for logging purposes
            Optional<Case> existingCaseOpt = caseService.getCaseById(id);

            Case updatedCase = caseService.updateCase(id, caseDetails);
            if (updatedCase != null) {
                // Create detailed log message showing what changed
                StringBuilder logMessage = new StringBuilder();
                logMessage.append(String.format("Case #%d updated by %s.", id, username));

                if (existingCaseOpt.isPresent()) {
                    Case existingCase = existingCaseOpt.get();

                    // Check what fields were changed
                    if (!existingCase.getCaseStatus().equals(updatedCase.getCaseStatus())) {
                        logMessage.append(String.format(" Status changed from %s to %s.",
                                existingCase.getCaseStatus(), updatedCase.getCaseStatus()));
                    }

                    if (!existingCase.getCaseType().equals(updatedCase.getCaseType())) {
                        logMessage.append(String.format(" Type changed from %s to %s.",
                                existingCase.getCaseType(), updatedCase.getCaseType()));
                    }

                    if (!existingCase.getCrimeLocation().equals(updatedCase.getCrimeLocation())) {
                        logMessage.append(String.format(" Location changed from %s to %s.",
                                existingCase.getCrimeLocation(), updatedCase.getCrimeLocation()));
                    }
                }

                // Log the case update
                logService.logAction(
                        username,
                        "CASE_UPDATED",
                        logMessage.toString()
                );

                return new ResponseEntity<>(updatedCase, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Log the error
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            logService.logAction(
                    username,
                    "CASE_UPDATE_ERROR",
                    String.format("Failed to update case #%d: %s", id, e.getMessage())
            );

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete case
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCase(@PathVariable("id") Long id) {
        try {
            // Get the logged-in user from session
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            // Get case details before deletion for logging
            Optional<Case> caseToDelete = caseService.getCaseById(id);

            boolean deleted = caseService.deleteCase(id);
            if (deleted) {
                // Log the case deletion with details
                String logMessage = String.format("Case #%d deleted by %s", id, username);
                if (caseToDelete.isPresent()) {
                    Case deletedCase = caseToDelete.get();
                    logMessage += String.format(". Deleted case details - Type: %s, Location: %s, Reporter: %s",
                            deletedCase.getCaseType(),
                            deletedCase.getCrimeLocation(),
                            deletedCase.getReporterName());
                }

                logService.logAction(
                        username,
                        "CASE_DELETED",
                        logMessage
                );

                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                // Log failed deletion attempt
                logService.logAction(
                        username,
                        "CASE_DELETE_FAILED",
                        String.format("Failed to delete case #%d by %s - Case not found", id, username)
                );

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Log the error
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            logService.logAction(
                    username,
                    "CASE_DELETE_ERROR",
                    String.format("Error deleting case #%d by %s: %s", id, username, e.getMessage())
            );

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get cases by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Case>> getCasesByStatus(@PathVariable("status") String status) {
        try {
            List<Case> cases = caseService.getCasesByStatus(status);

            // Log the search activity
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            logService.logAction(
                    username,
                    "CASE_SEARCH",
                    String.format("Cases searched by status '%s' by %s. Found %d cases.",
                            status, username, cases.size())
            );

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

            // Log the search activity
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            logService.logAction(
                    username,
                    "CASE_SEARCH",
                    String.format("Cases searched by officer badge '%s' by %s. Found %d cases.",
                            badgeNumber, username, cases.size())
            );

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
            // Get the logged-in user from session
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            // Get existing case to log the status change
            Optional<Case> existingCaseOpt = caseService.getCaseById(id);
            String oldStatus = existingCaseOpt.isPresent() ? existingCaseOpt.get().getCaseStatus() : "Unknown";

            Case updatedCase = caseService.updateCaseStatus(id, status);
            if (updatedCase != null) {
                // Log the status change
                logService.logAction(
                        username,
                        "CASE_STATUS_UPDATED",
                        String.format("Case #%d status updated by %s from '%s' to '%s'",
                                id, username, oldStatus, status)
                );

                return new ResponseEntity<>(updatedCase, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Log the error
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            logService.logAction(
                    username,
                    "CASE_STATUS_UPDATE_ERROR",
                    String.format("Failed to update status for case #%d by %s: %s", id, username, e.getMessage())
            );

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
