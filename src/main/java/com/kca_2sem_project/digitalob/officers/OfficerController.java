package com.kca_2sem_project.digitalob.officers;

import com.kca_2sem_project.digitalob.dto.OfficerDTOs;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/officers")
@CrossOrigin(origins = "*") // Enable CORS for frontend access
public class OfficerController {

    @Autowired
    private OfficerService officerService;

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody OfficerDTOs.LoginRequest loginRequest) {
        Optional<Officer> officerOpt = officerService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (officerOpt.isPresent()) {
            Officer officer = officerOpt.get();
            OfficerDTOs.LoginResponse response = new OfficerDTOs.LoginResponse(
                    officer.getId(),
                    officer.getName(),
                    officer.getRole(),
                    officer.getStatus(),
                    "Login successful"
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new OfficerDTOs.ApiResponse("Invalid username or password", false));
        }
    }
    // Logout endpoint
    @PostMapping("/logout")
    public ResponseEntity<OfficerDTOs.ApiResponse> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Invalidate the session
        }
        return ResponseEntity.ok(new OfficerDTOs.ApiResponse("Logout successful", true));
    }

    // Create new officer
    @PostMapping
    public ResponseEntity<OfficerDTOs.OfficerResponse> createOfficer(@RequestBody Officer officer) {
        Officer savedOfficer = officerService.createOfficer(officer);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToOfficerResponse(savedOfficer));
    }

    // Get all officers
    @GetMapping
    public ResponseEntity<List<OfficerDTOs.OfficerResponse>> getAllOfficers() {
        List<Officer> officers = officerService.getAllOfficers();
        List<OfficerDTOs.OfficerResponse> officerResponses = officers.stream()
                .map(this::convertToOfficerResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(officerResponses);
    }

    // Get officer by ID
    @GetMapping("/{id}")
    public ResponseEntity<OfficerDTOs.OfficerResponse> getOfficerById(@PathVariable Long id) {
        Optional<Officer> officerOpt = officerService.getOfficerById(id);
        return officerOpt.map(officer -> ResponseEntity.ok(convertToOfficerResponse(officer)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Update officer
    @PutMapping("/{id}")
    public ResponseEntity<OfficerDTOs.OfficerResponse> updateOfficer(@PathVariable Long id, @RequestBody Officer officerDetails) {
        Officer updatedOfficer = officerService.updateOfficer(id, officerDetails);
        return ResponseEntity.ok(convertToOfficerResponse(updatedOfficer));
    }

    // Delete officer
    @DeleteMapping("/{id}")
    public ResponseEntity<OfficerDTOs.ApiResponse> deleteOfficer(@PathVariable Long id) {
        officerService.deleteOfficer(id);
        return ResponseEntity.ok(new OfficerDTOs.ApiResponse("Officer deleted successfully", true));
    }

    // Helper method to convert Officer to OfficerResponse
    private OfficerDTOs.OfficerResponse convertToOfficerResponse(Officer officer) {
        return new OfficerDTOs.OfficerResponse(
                officer.getId(),
                officer.getName(),
                officer.getIdNumber(),
                officer.getOfficerNumber(),
                officer.getRole(),
                officer.getGender(),
                officer.getSpecialization(),
                officer.getEmail(),
                officer.getUsername(),
                officer.getStatus()
        );
    }
}
