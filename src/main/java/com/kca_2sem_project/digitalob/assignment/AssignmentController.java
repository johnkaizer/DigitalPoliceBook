package com.kca_2sem_project.digitalob.assignment;
import com.kca_2sem_project.digitalob.auditlogs.LogService;
import com.kca_2sem_project.digitalob.usersmanagement.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "*")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private LogService logService;

    @Autowired
    private HttpSession session;

    // Create a new assignment
    @PostMapping
    public ResponseEntity<Assignment> createAssignment(@RequestBody Map<String, Object> requestData) {
        try {
            // Get the logged-in admin from session
            User loggedInAdmin = (User) session.getAttribute("loggedInUser");
            if (loggedInAdmin == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            // Extract parameters from request body
            Long caseId = Long.valueOf(requestData.get("caseId").toString());
            Long officerId = Long.valueOf(requestData.get("assignedOfficerId").toString()); // Note: frontend sends "assignedOfficerId"
            String priority = requestData.getOrDefault("priority", "MEDIUM").toString();
            String assignmentNotes = requestData.get("assignmentNotes") != null ?
                    requestData.get("assignmentNotes").toString() : null;

            LocalDateTime dueDate = null;
            if (requestData.get("dueDate") != null && !requestData.get("dueDate").toString().isEmpty()) {
                String dueDateStr = requestData.get("dueDate").toString();
                // Handle the date format from frontend (e.g., "2025-06-30T08:52")
                if (dueDateStr.length() == 16) { // Format: "2025-06-30T08:52"
                    dueDateStr += ":00"; // Add seconds
                }
                dueDate = LocalDateTime.parse(dueDateStr);
            }

            String adminName = loggedInAdmin.getFullName();
            Long assignedById = loggedInAdmin.getId();

            Assignment savedAssignment = assignmentService.createAssignment(
                    caseId, officerId, assignedById, priority, assignmentNotes, dueDate);

            // Log the assignment creation
            logService.logAction(
                    adminName,
                    "CASE_ASSIGNED",
                    String.format("Case #%d assigned to Officer %s (Badge: %s) by %s. Priority: %s",
                            caseId,
                            savedAssignment.getOfficerName(),
                            savedAssignment.getOfficerBadgeNumber(),
                            adminName,
                            priority)
            );

            return new ResponseEntity<>(savedAssignment, HttpStatus.CREATED);

        } catch (NumberFormatException e) {
            User loggedInAdmin = (User) session.getAttribute("loggedInUser");
            String adminName = loggedInAdmin != null ? loggedInAdmin.getFullName() : "Unknown Admin";

            logService.logAction(
                    adminName,
                    "ASSIGNMENT_CREATE_ERROR",
                    "Failed to create assignment: Invalid number format - " + e.getMessage()
            );

            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        } catch (DateTimeParseException e) {
            User loggedInAdmin = (User) session.getAttribute("loggedInUser");
            String adminName = loggedInAdmin != null ? loggedInAdmin.getFullName() : "Unknown Admin";

            logService.logAction(
                    adminName,
                    "ASSIGNMENT_CREATE_ERROR",
                    "Failed to create assignment: Invalid date format - " + e.getMessage()
            );

            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        } catch (RuntimeException e) {
            User loggedInAdmin = (User) session.getAttribute("loggedInUser");
            String adminName = loggedInAdmin != null ? loggedInAdmin.getFullName() : "Unknown Admin";

            logService.logAction(
                    adminName,
                    "ASSIGNMENT_CREATE_ERROR",
                    "Failed to create assignment: " + e.getMessage()
            );

            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all assignments
    @GetMapping
    public ResponseEntity<List<Assignment>> getAllAssignments() {
        try {
            List<Assignment> assignments = assignmentService.getAllAssignments();
            return new ResponseEntity<>(assignments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get assignment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getAssignmentById(@PathVariable("id") Long id) {
        try {
            Optional<Assignment> assignmentData = assignmentService.getAssignmentById(id);
            if (assignmentData.isPresent()) {
                // Log assignment access
                User loggedInUser = (User) session.getAttribute("loggedInUser");
                String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

                logService.logAction(
                        username,
                        "ASSIGNMENT_VIEWED",
                        String.format("Assignment #%d viewed by %s", id, username)
                );

                return new ResponseEntity<>(assignmentData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get assignments by case ID
    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<Assignment>> getAssignmentsByCaseId(@PathVariable("caseId") Long caseId) {
        try {
            List<Assignment> assignments = assignmentService.getAssignmentsByCaseId(caseId);
            return new ResponseEntity<>(assignments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get assignments by officer ID
    @GetMapping("/officer/{officerId}")
    public ResponseEntity<List<Assignment>> getAssignmentsByOfficerId(@PathVariable("officerId") Long officerId) {
        try {
            List<Assignment> assignments = assignmentService.getAssignmentsByOfficerId(officerId);

            // Log the search
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            logService.logAction(
                    username,
                    "ASSIGNMENT_SEARCH",
                    String.format("Assignments searched by officer ID %d by %s. Found %d assignments.",
                            officerId, username, assignments.size())
            );

            return new ResponseEntity<>(assignments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get assignments by officer badge number
    @GetMapping("/officer/badge/{badgeNumber}")
    public ResponseEntity<List<Assignment>> getAssignmentsByOfficerBadge(@PathVariable("badgeNumber") String badgeNumber) {
        try {
            List<Assignment> assignments = assignmentService.getAssignmentsByOfficerBadge(badgeNumber);

            // Log the search
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            logService.logAction(
                    username,
                    "ASSIGNMENT_SEARCH",
                    String.format("Assignments searched by badge %s by %s. Found %d assignments.",
                            badgeNumber, username, assignments.size())
            );

            return new ResponseEntity<>(assignments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get assignments by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Assignment>> getAssignmentsByStatus(@PathVariable("status") String status) {
        try {
            List<Assignment> assignments = assignmentService.getAssignmentsByStatus(status);

            // Log the search
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            logService.logAction(
                    username,
                    "ASSIGNMENT_SEARCH",
                    String.format("Assignments searched by status '%s' by %s. Found %d assignments.",
                            status, username, assignments.size())
            );

            return new ResponseEntity<>(assignments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get assignments by priority
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Assignment>> getAssignmentsByPriority(@PathVariable("priority") String priority) {
        try {
            List<Assignment> assignments = assignmentService.getAssignmentsByPriority(priority);
            return new ResponseEntity<>(assignments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get active assignments for an officer
    @GetMapping("/officer/{officerId}/active")
    public ResponseEntity<List<Assignment>> getActiveAssignmentsByOfficer(@PathVariable("officerId") Long officerId) {
        try {
            List<Assignment> assignments = assignmentService.getActiveAssignmentsByOfficer(officerId);
            return new ResponseEntity<>(assignments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get completed assignments for an officer
    @GetMapping("/officer/{officerId}/completed")
    public ResponseEntity<List<Assignment>> getCompletedAssignmentsByOfficer(@PathVariable("officerId") Long officerId) {
        try {
            List<Assignment> assignments = assignmentService.getCompletedAssignmentsByOfficer(officerId);
            return new ResponseEntity<>(assignments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get overdue assignments
    @GetMapping("/overdue")
    public ResponseEntity<List<Assignment>> getOverdueAssignments() {
        try {
            List<Assignment> assignments = assignmentService.getOverdueAssignments();

            // Log the overdue check
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            logService.logAction(
                    username,
                    "OVERDUE_ASSIGNMENTS_CHECKED",
                    String.format("Overdue assignments checked by %s. Found %d overdue assignments.",
                            username, assignments.size())
            );

            return new ResponseEntity<>(assignments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update assignment status - FIXED: Added PUT mapping to match frontend expectations
    @PutMapping("/{id}/status")
    @PatchMapping("/{id}/status") // Keep both for compatibility
    public ResponseEntity<Assignment> updateAssignmentStatus(
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> statusUpdate) {
        try {
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            // Get status from request body
            String status = statusUpdate.get("status");
            if (status == null || status.trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Get existing assignment to log the status change
            Optional<Assignment> existingAssignmentOpt = assignmentService.getAssignmentById(id);
            String oldStatus = existingAssignmentOpt.isPresent() ? existingAssignmentOpt.get().getAssignmentStatus() : "Unknown";

            Assignment updatedAssignment = assignmentService.updateAssignmentStatus(id, status);
            if (updatedAssignment != null) {
                // Log the status change
                logService.logAction(
                        username,
                        "ASSIGNMENT_STATUS_UPDATED",
                        String.format("Assignment #%d status updated by %s from '%s' to '%s'",
                                id, username, oldStatus, status)
                );

                return new ResponseEntity<>(updatedAssignment, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update assignment priority
    @PatchMapping("/{id}/priority")
    public ResponseEntity<Assignment> updateAssignmentPriority(@PathVariable("id") Long id, @RequestParam String priority) {
        try {
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            Assignment updatedAssignment = assignmentService.updateAssignmentPriority(id, priority);
            if (updatedAssignment != null) {
                logService.logAction(
                        username,
                        "ASSIGNMENT_PRIORITY_UPDATED",
                        String.format("Assignment #%d priority updated to '%s' by %s", id, priority, username)
                );

                return new ResponseEntity<>(updatedAssignment, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update assignment due date
    @PatchMapping("/{id}/due-date")
    public ResponseEntity<Assignment> updateAssignmentDueDate(
            @PathVariable("id") Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate) {
        try {
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            Assignment updatedAssignment = assignmentService.updateAssignmentDueDate(id, dueDate);
            if (updatedAssignment != null) {
                logService.logAction(
                        username,
                        "ASSIGNMENT_DUE_DATE_UPDATED",
                        String.format("Assignment #%d due date updated by %s", id, username)
                );

                return new ResponseEntity<>(updatedAssignment, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update assignment notes
    @PatchMapping("/{id}/notes")
    public ResponseEntity<Assignment> updateAssignmentNotes(@PathVariable("id") Long id, @RequestBody String notes) {
        try {
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            Assignment updatedAssignment = assignmentService.updateAssignmentNotes(id, notes);
            if (updatedAssignment != null) {
                logService.logAction(
                        username,
                        "ASSIGNMENT_NOTES_UPDATED",
                        String.format("Assignment #%d notes updated by %s", id, username)
                );

                return new ResponseEntity<>(updatedAssignment, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Reassign case to different officer - FIXED: Changed to accept JSON body instead of request params
    @PostMapping("/{assignmentId}/reassign")
    public ResponseEntity<Assignment> reassignCase(
            @PathVariable("assignmentId") Long assignmentId,
            @RequestBody Map<String, Object> requestData) {

        try {
            User loggedInAdmin = (User) session.getAttribute("loggedInUser");
            if (loggedInAdmin == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            // Extract parameters from request body
            Long newOfficerId = Long.valueOf(requestData.get("newOfficerId").toString());
            String reason = requestData.get("reason").toString();

            if (newOfficerId == null || reason == null || reason.trim().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            String adminName = loggedInAdmin.getFullName();
            Long reassignedById = loggedInAdmin.getId();

            Assignment newAssignment = assignmentService.reassignCase(assignmentId, newOfficerId, reassignedById, reason);
            if (newAssignment != null) {
                logService.logAction(
                        adminName,
                        "CASE_REASSIGNED",
                        String.format("Assignment #%d reassigned to Officer %s (Badge: %s) by %s. Reason: %s",
                                assignmentId,
                                newAssignment.getOfficerName(),
                                newAssignment.getOfficerBadgeNumber(),
                                adminName,
                                reason)
                );

                return new ResponseEntity<>(newAssignment, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete assignment
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteAssignment(@PathVariable("id") Long id) {
        try {
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            if (loggedInUser == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            String username = loggedInUser.getFullName();

            boolean deleted = assignmentService.deleteAssignment(id);
            if (deleted) {
                logService.logAction(
                        username,
                        "ASSIGNMENT_DELETED",
                        String.format("Assignment #%d deleted by %s", id, username)
                );
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get assignments by department
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Assignment>> getAssignmentsByDepartment(@PathVariable("department") String department) {
        try {
            List<Assignment> assignments = assignmentService.getAssignmentsByDepartment(department);

            // Log the search
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            logService.logAction(
                    username,
                    "ASSIGNMENT_SEARCH",
                    String.format("Assignments searched by department '%s' by %s. Found %d assignments.",
                            department, username, assignments.size())
            );

            return new ResponseEntity<>(assignments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get assignments by case type
    @GetMapping("/case-type/{caseType}")
    public ResponseEntity<List<Assignment>> getAssignmentsByCaseType(@PathVariable("caseType") String caseType) {
        try {
            List<Assignment> assignments = assignmentService.getAssignmentsByCaseType(caseType);
            return new ResponseEntity<>(assignments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get assignment statistics by status
    @GetMapping("/stats/status/{status}")
    public ResponseEntity<Long> getAssignmentCountByStatus(@PathVariable("status") String status) {
        try {
            long count = assignmentService.getAssignmentCountByStatus(status);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get assignment statistics - overall counts
    @GetMapping("/stats")
    public ResponseEntity<Object> getAssignmentStatistics() {
        try {
            // Create a simple statistics object
            var stats = new java.util.HashMap<String, Object>();
            stats.put("total", assignmentService.getAllAssignments().size());
            stats.put("assigned", assignmentService.getAssignmentCountByStatus("ASSIGNED"));
            stats.put("inProgress", assignmentService.getAssignmentCountByStatus("IN_PROGRESS"));
            stats.put("completed", assignmentService.getAssignmentCountByStatus("COMPLETED"));
            stats.put("reassigned", assignmentService.getAssignmentCountByStatus("REASSIGNED"));
            stats.put("overdue", assignmentService.getOverdueAssignments().size());

            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}