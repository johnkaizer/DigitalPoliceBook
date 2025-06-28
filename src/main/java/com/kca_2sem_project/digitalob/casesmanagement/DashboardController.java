package com.kca_2sem_project.digitalob.casesmanagement;

import com.kca_2sem_project.digitalob.usersmanagement.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private CaseService caseService;

    @Autowired
    private HttpSession session;

    // You'll need to inject your officer service here
    // @Autowired
    // private OfficerService officerService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        try {
            // Get the logged-in user from session
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            DashboardStats stats = new DashboardStats();

            // Get case statistics
            stats.setOpenCases(caseService.getOpenCasesCount());
            stats.setResolvedCases(caseService.getResolvedCasesCount());
            stats.setLocationsMapped(caseService.getUniqueLocationsCount());

            stats.setActiveOfficers(caseService.getUniqueOfficersCount());


            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            // Log the error
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<RecentActivity>> getRecentActivity() {
        try {
            // Get the logged-in user from session
            User loggedInUser = (User) session.getAttribute("loggedInUser");
            String username = loggedInUser != null ? loggedInUser.getFullName() : "Unknown User";

            List<RecentActivity> activities = caseService.getRecentCaseActivity(10);

            return new ResponseEntity<>(activities, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DTO Classes
    public static class DashboardStats {
        private Long activeOfficers;
        private Long openCases;
        private Long resolvedCases;
        private Long locationsMapped;

        // Constructors
        public DashboardStats() {}

        public DashboardStats(Long activeOfficers, Long openCases, Long resolvedCases, Long locationsMapped) {
            this.activeOfficers = activeOfficers;
            this.openCases = openCases;
            this.resolvedCases = resolvedCases;
            this.locationsMapped = locationsMapped;
        }

        // Getters and Setters
        public Long getActiveOfficers() { return activeOfficers; }
        public void setActiveOfficers(Long activeOfficers) { this.activeOfficers = activeOfficers; }

        public Long getOpenCases() { return openCases; }
        public void setOpenCases(Long openCases) { this.openCases = openCases; }

        public Long getResolvedCases() { return resolvedCases; }
        public void setResolvedCases(Long resolvedCases) { this.resolvedCases = resolvedCases; }

        public Long getLocationsMapped() { return locationsMapped; }
        public void setLocationsMapped(Long locationsMapped) { this.locationsMapped = locationsMapped; }
    }

    public static class RecentActivity {
        private String activityType;
        private String description;
        private String timestamp;
        private String icon;
        private String iconColor;

        // Constructors
        public RecentActivity() {}

        public RecentActivity(String activityType, String description, String timestamp, String icon, String iconColor) {
            this.activityType = activityType;
            this.description = description;
            this.timestamp = timestamp;
            this.icon = icon;
            this.iconColor = iconColor;
        }

        // Getters and Setters
        public String getActivityType() { return activityType; }
        public void setActivityType(String activityType) { this.activityType = activityType; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public String getIconColor() { return iconColor; }
        public void setIconColor(String iconColor) { this.iconColor = iconColor; }
    }
}
