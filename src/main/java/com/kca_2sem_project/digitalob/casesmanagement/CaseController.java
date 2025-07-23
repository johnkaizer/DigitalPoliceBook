package com.kca_2sem_project.digitalob.casesmanagement;

import com.kca_2sem_project.digitalob.assignment.AssignmentService;
import com.kca_2sem_project.digitalob.auditlogs.LogService;
import com.kca_2sem_project.digitalob.config.SMSService;
import com.kca_2sem_project.digitalob.usersmanagement.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cases")
@CrossOrigin(origins = "*")
public class CaseController {

    @Autowired
    private CaseService caseService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private LogService logService;

    @Autowired
    private SMSService smsService; // Add SMS service

    @Autowired
    private HttpSession session;

    // Create a new case with SMS notification
    @PostMapping
    public ResponseEntity<Case> createCase(@RequestBody Case caseEntity) {
        try {
            // Get the logged-in user from session
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            Case savedCase = caseService.createCase(caseEntity);

            // Send SMS notification to reporter if phone number is provided
            boolean smsStatus = false;
            if (savedCase.getReporterPhone() != null && !savedCase.getReporterPhone().trim().isEmpty()) {
                smsStatus = smsService.sendCaseCreationSMS(
                        savedCase.getReporterPhone(),
                        savedCase.getReporterName(),
                        savedCase.getId()
                );
            }

            // Log case creation with SMS status
            logService.logAction(
                    username,
                    "CASE_CREATED",
                    String.format("Case #%d created by %s. Reporter: %s. SMS Status: %s",
                            savedCase.getId(), username, savedCase.getReporterName(),
                            smsStatus ? "SENT" : "FAILED")
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

                boolean statusChanged = false;
                if (existingCaseOpt.isPresent()) {
                    Case existingCase = existingCaseOpt.get();

                    // Check what fields were changed
                    if (!existingCase.getCaseStatus().equals(updatedCase.getCaseStatus())) {
                        logMessage.append(String.format(" Status changed from %s to %s.",
                                existingCase.getCaseStatus(), updatedCase.getCaseStatus()));
                        statusChanged = true;
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

                // Send SMS notification if case status changed to CLOSED
                boolean smsStatus = false;
                if (statusChanged && "CLOSED".equals(updatedCase.getCaseStatus())) {
                    if (updatedCase.getReporterPhone() != null && !updatedCase.getReporterPhone().trim().isEmpty()) {
                        String closureMessage = String.format(
                                "Dear %s, your case OB Number %d has been closed at Ruaraka Police Station(demo university project). " +
                                        "Thank you for your cooperation.",
                                updatedCase.getReporterName(),
                                updatedCase.getId()
                        );
                        smsStatus = smsService.sendCustomSms(updatedCase.getReporterPhone(), closureMessage);
                        logMessage.append(String.format(" SMS Status: %s", smsStatus ? "SENT" : "FAILED"));
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
    public ResponseEntity<Map<String, String>> deleteCase(@PathVariable("id") Long id) {
        try {
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            // Check if case has assignments
            Optional<Case> caseToDelete = caseService.getCaseById(id);
            if (!caseToDelete.isPresent()) {
                return new ResponseEntity<>(Map.of("error", "Case not found"), HttpStatus.NOT_FOUND);
            }

            // Count assignments for this case
            long assignmentCount = assignmentService.getAssignmentsByCaseId(id).size();

            boolean deleted = caseService.deleteCase(id);
            if (deleted) {
                String logMessage = String.format("Case #%d deleted by %s", id, username);
                if (assignmentCount > 0) {
                    logMessage += String.format(" (Warning: %d related assignments were also deleted)", assignmentCount);
                }

                Case deletedCase = caseToDelete.get();
                logMessage += String.format(". Deleted case details - Type: %s, Location: %s, Reporter: %s",
                        deletedCase.getCaseType(),
                        deletedCase.getCrimeLocation(),
                        deletedCase.getReporterName());

                logService.logAction(username, "CASE_DELETED", logMessage);

                Map<String, String> response = new HashMap<>();
                response.put("message", "Case deleted successfully");
                if (assignmentCount > 0) {
                    response.put("warning", assignmentCount + " related assignments were also deleted");
                }
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                logService.logAction(username, "CASE_DELETE_FAILED",
                        String.format("Failed to delete case #%d by %s - Case not found", id, username));
                return new ResponseEntity<>(Map.of("error", "Failed to delete case"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            logService.logAction(username, "CASE_DELETE_ERROR",
                    String.format("Error deleting case #%d by %s: %s", id, username, e.getMessage()));
            return new ResponseEntity<>(Map.of("error", "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
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

    // Update case status only (with SMS notification)
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
                // Send SMS notification for status updates
                boolean smsStatus = false;
                if (updatedCase.getReporterPhone() != null && !updatedCase.getReporterPhone().trim().isEmpty()) {
                    String statusMessage = String.format(
                            "Dear %s, your case OB Number %d status has been updated to %s at Ruaraka Police Station. " +
                                    "Thank you.",
                            updatedCase.getReporterName(),
                            updatedCase.getId(),
                            status
                    );

                    smsStatus = smsService.sendCustomSms(updatedCase.getReporterPhone(), statusMessage);
                }

                // Log the status change
                logService.logAction(
                        username,
                        "CASE_STATUS_UPDATED",
                        String.format("Case #%d status updated by %s from '%s' to '%s'. SMS Status: %s",
                                id, username, oldStatus, status, smsStatus ? "SENT" : "FAILED")
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

    @GetMapping("/mapping")
    public ResponseEntity<List<CaseLocationMapping>> getCasesMapping() {
        try {
            List<CaseLocationMapping> locationMappings = caseService.getCasesLocationMapping();

            // Log the mapping access
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            logService.logAction(
                    username,
                    "CASE_MAPPING_VIEWED",
                    String.format("Cases mapping accessed by %s. Total locations: %d",
                            username, locationMappings.size())
            );

            return new ResponseEntity<>(locationMappings, HttpStatus.OK);
        } catch (Exception e) {
            // Log the error
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            logService.logAction(
                    username,
                    "CASE_MAPPING_ERROR",
                    String.format("Error accessing cases mapping by %s: %s", username, e.getMessage())
            );

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DTO class for location mapping response
    public static class CaseLocationMapping {
        private String location;
        private Long caseCount;
        private List<CaseTypeSummary> caseTypes;
        private String mostRecentCaseDate;

        // Constructors
        public CaseLocationMapping() {}

        public CaseLocationMapping(String location, Long caseCount, List<CaseTypeSummary> caseTypes, String mostRecentCaseDate) {
            this.location = location;
            this.caseCount = caseCount;
            this.caseTypes = caseTypes;
            this.mostRecentCaseDate = mostRecentCaseDate;
        }

        // Getters and Setters
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public Long getCaseCount() { return caseCount; }
        public void setCaseCount(Long caseCount) { this.caseCount = caseCount; }

        public List<CaseTypeSummary> getCaseTypes() { return caseTypes; }
        public void setCaseTypes(List<CaseTypeSummary> caseTypes) { this.caseTypes = caseTypes; }

        public String getMostRecentCaseDate() { return mostRecentCaseDate; }
        public void setMostRecentCaseDate(String mostRecentCaseDate) { this.mostRecentCaseDate = mostRecentCaseDate; }
    }

    public static class CaseTypeSummary {
        private String caseType;
        private Long count;

        public CaseTypeSummary() {}

        public CaseTypeSummary(String caseType, Long count) {
            this.caseType = caseType;
            this.count = count;
        }

        // Getters and Setters
        public String getCaseType() { return caseType; }
        public void setCaseType(String caseType) { this.caseType = caseType; }

        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }
}