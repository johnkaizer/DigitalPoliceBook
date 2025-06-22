package com.kca_2sem_project.digitalob.licencemanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/licenses")
public class LicenseController {

    @Autowired
    private LicenseService licenseService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createLicense(@RequestBody Map<String, String> request) {
        String licenseValue = request.get("licenseValue");
        License createdLicense = licenseService.createLicense(licenseValue);
        Map<String, String> response = new HashMap<>();
        response.put("message", "License created successfully");
        response.put("licenseId", String.valueOf(createdLicense.getId()));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/activate")
    public ResponseEntity<Map<String, String>> activateLicense(@RequestBody Map<String, String> request) {
        String licenseValue = request.get("licenseValue");
        Map<String, String> response = new HashMap<>();

        try {
            licenseService.activateLicense(licenseValue);
            response.put("message", "License activated successfully. Application unlocked forever.");
            response.put("status", "ACTIVE");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            response.put("status", "INACTIVE");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    @GetMapping
    public ResponseEntity<License> getLicense() {
        License license = licenseService.getCurrentLicense();
        return ResponseEntity.ok(license);
    }

    @PostMapping("/expire-check")
    public ResponseEntity<Map<String, String>> checkAndExpireLicenses() {
        licenseService.checkAndExpireLicenses();
        Map<String, String> response = new HashMap<>();
        response.put("message", "License status updated successfully for expired licenses");
        return ResponseEntity.ok(response);
    }
}
