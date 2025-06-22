package com.kca_2sem_project.digitalob.licencemanagement;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LicenseService {

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeLicense() {
        // Ensure only one license exists
        if (licenseRepository.count() == 0) {
            createLicense("SL22XK9LPE@2025"); // Default license value
        } else {
            checkAndExpireLicenses();
        }
    }

    public License createLicense(String licenseValue) {
        // Ensure only one license is created
        if (licenseRepository.count() > 0) {
            throw new RuntimeException("A license already exists. Only one license is allowed.");
        }

        License license = new License();
        license.setEncryptedValue(passwordEncoder.encode(licenseValue)); // Encrypt the license value
        license.setCreatedDatetime(LocalDateTime.now());
        license.setStatus("INACTIVE");
        return licenseRepository.save(license);
    }

    public boolean activateLicense(String licenseValue) {
        Optional<License> optionalLicense = licenseRepository.findFirstByOrderByCreatedDatetimeDesc();

        if (optionalLicense.isPresent()) {
            License license = optionalLicense.get();

            // Match the entered value with the stored encrypted value
            if (passwordEncoder.matches(licenseValue, license.getEncryptedValue())) {
                license.setStatus("ACTIVE");
                license.setExpiryDatetime(LocalDateTime.now().plusYears(50)); // Extend expiry by 50 years
                license.setUpdatedDatetime(LocalDateTime.now());
                licenseRepository.save(license);
                return true;
            } else {
                throw new RuntimeException("Invalid license value.");
            }
        } else {
            throw new RuntimeException("No license found.");
        }
    }

    @Transactional
    public void checkAndExpireLicenses() {
        Optional<License> optionalLicense = licenseRepository.findFirstByOrderByCreatedDatetimeDesc();

        if (optionalLicense.isPresent()) {
            License license = optionalLicense.get();

            if ("ACTIVE".equals(license.getStatus()) &&
                    LocalDateTime.now().isAfter(license.getExpiryDatetime())) {
                license.setStatus("INACTIVE");
                licenseRepository.save(license);
            }
        }
    }

    public License getCurrentLicense() {
        return licenseRepository.findFirstByOrderByCreatedDatetimeDesc()
                .orElseThrow(() -> new RuntimeException("No license found."));
    }
}
