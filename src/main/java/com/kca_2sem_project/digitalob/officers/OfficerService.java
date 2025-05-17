package com.kca_2sem_project.digitalob.officers;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfficerService {

    @Autowired
    private OfficerRepository officerRepository;

    // Create default admin user on application startup
    @PostConstruct
    public void init() {
        if (!officerRepository.existsByUsername("admin")) {
            Officer admin = new Officer();
            admin.setName("System Administrator");
            admin.setIdNumber("ADMIN001");
            admin.setOfficerNumber("ADMIN001");
            admin.setRole("Admin");
            admin.setGender("N/A");
            admin.setSpecialization("N/A");
            admin.setEmail("admin@policestation.com");
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setStatus("ACTIVE");

            officerRepository.save(admin);
            System.out.println("Default admin user created");
        }
    }

    // Create a new officer
    public Officer createOfficer(Officer officer) {
        // Set default status if not provided
        if (officer.getStatus() == null || officer.getStatus().isEmpty()) {
            officer.setStatus("ACTIVE");
        }
        return officerRepository.save(officer);
    }

    // Get all officers
    public List<Officer> getAllOfficers() {
        return officerRepository.findAll();
    }

    // Get officer by ID
    public Optional<Officer> getOfficerById(Long id) {
        return officerRepository.findById(id);
    }

    // Update officer
    public Officer updateOfficer(Long id, Officer officerDetails) {
        Officer officer = officerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Officer not found with id: " + id));

        officer.setName(officerDetails.getName());
        officer.setIdNumber(officerDetails.getIdNumber());
        officer.setOfficerNumber(officerDetails.getOfficerNumber());
        officer.setRole(officerDetails.getRole());
        officer.setGender(officerDetails.getGender());
        officer.setSpecialization(officerDetails.getSpecialization());
        officer.setEmail(officerDetails.getEmail());
        officer.setUsername(officerDetails.getUsername());
        // Only update password if provided
        if (officerDetails.getPassword() != null && !officerDetails.getPassword().isEmpty()) {
            officer.setPassword(officerDetails.getPassword());
        }
        officer.setStatus(officerDetails.getStatus());

        return officerRepository.save(officer);
    }

    // Delete officer
    public void deleteOfficer(Long id) {
        Officer officer = officerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Officer not found with id: " + id));
        officerRepository.delete(officer);
    }

    // Login method
    public Optional<Officer> login(String username, String password) {
        Optional<Officer> officer = officerRepository.findByUsername(username);
        if (officer.isPresent() && officer.get().getPassword().equals(password) &&
                "ACTIVE".equals(officer.get().getStatus())) {
            return officer;
        }
        return Optional.empty();
    }
}