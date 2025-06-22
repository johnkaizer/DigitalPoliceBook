package com.kca_2sem_project.digitalob.usersmanagement;

import com.kca_2sem_project.digitalob.auditlogs.LogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private HttpSession session;

    @Autowired
    private LogService logService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> addOfficer(
            @RequestPart("user") User user,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        User createdUser = userService.addOfficer(user, imageFile);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> updateOfficer(
            @PathVariable Long id,
            @RequestPart("user") User userDetails,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        User updatedUser = userService.updateOfficer(id, userDetails, imageFile);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOfficer(@PathVariable Long id) {
        userService.deleteOfficer(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getOfficer(@PathVariable Long id) {
        User user = userService.getOfficer(id);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/officer-image/{fileName}")
    public ResponseEntity<UrlResource> getOfficerImage(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("./uploads/officers/").resolve(fileName).normalize();
            UrlResource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String password) {
        User user = userService.authenticate(username, password);
        session.setAttribute("loggedInUser", user); // Set the user in the session

        // Log the login action
        logService.logAction(
                username,
                "LOGIN",
                "User " + user.getFullName() + " logged in."
        );

        Map<String, String> response = new HashMap<>();
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            response.put("redirectUrl", "/admin_dashboard");
        } else if ("Officer".equalsIgnoreCase(user.getRole())) {
            response.put("redirectUrl", "/officer_dashboard");
        } else {
            response.put("redirectUrl", "/login_error");
        }

        return ResponseEntity.ok(response);
    }

    // Logout Endpoint
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        User user = (User) session.getAttribute("loggedInUser"); // Get the logged-in user

        if (user != null) {
            // Log the logout action
            logService.logAction(
                    user.getFullName(),
                    "LOGOUT",
                    "User " + user.getFullName() + " logged out."
            );

            session.invalidate(); // Invalidate the session
        }

        return ResponseEntity.ok("Logged out successfully");
    }
    // 3. Get Logged-In User Details
    @GetMapping("/me")
    public ResponseEntity<User> getLoggedInUser() {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            return ResponseEntity.ok(loggedInUser);
        }
        return ResponseEntity.status(401).body(null); // Unauthorized
    }

    // 4. Get Logged-In User ID
    @GetMapping("/me/id")
    public ResponseEntity<Long> getLoggedInUserId() {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            return ResponseEntity.ok(loggedInUser.getId());
        }
        return ResponseEntity.status(401).body(null); // Unauthorized
    }
    @PutMapping("/me")
    public ResponseEntity<User> updateLoggedInUser(
            @RequestPart("user") User userDetails,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User updatedUser = userService.updateOfficer(loggedInUser.getId(), userDetails, imageFile);
        session.setAttribute("loggedInUser", updatedUser); // Update session
        return ResponseEntity.ok(updatedUser);
    }
    @GetMapping("/role/cashier")
    public ResponseEntity<List<User>> getUsersWithCashierRole() {
        List<User> cashiers = userService.getUsersByRole("CASHIER");
        return ResponseEntity.ok(cashiers);
    }

    @PostMapping("/verify-reset")
    public ResponseEntity<?> verifyResetCredentials(
            @RequestParam String username,
            @RequestParam String idNumber) {
        try {
            boolean isValid = userService.verifyResetCredentials(username, idNumber);
            return ResponseEntity.ok().body(Map.of("valid", isValid));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam String username,
            @RequestParam String idNumber,
            @RequestParam String newPassword) {
        try {
            userService.resetPassword(username, idNumber, newPassword);
            return ResponseEntity.ok().body(Map.of("message", "Password reset successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/count")
    public ResponseEntity<Long> getUserCount() {
        return ResponseEntity.ok(userService.getUserCount());
    }

}
