package com.kca_2sem_project.digitalob.utils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final String logoUploadDir = "./uploads/logos/";
    private final String employeeImagesDir = "./uploads/officers/";
    private final String productImagesDir = "./uploads/products/";

    @PostConstruct
    public void init() {
        try {
            // Create all directories
            Files.createDirectories(Paths.get(logoUploadDir));
            Files.createDirectories(Paths.get(employeeImagesDir));
            Files.createDirectories(Paths.get(productImagesDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directories!");
        }
    }

    // Method for storing logos
    public String storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            // Generate unique filename
            String fileName = UUID.randomUUID().toString() + "_" +
                    StringUtils.cleanPath(file.getOriginalFilename());

            // Resolve the file path
            Path targetLocation = Paths.get(logoUploadDir).resolve(fileName);

            // Copy the file
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file", ex);
        }
    }

    // New method for storing employee images
    public String storeEmployeeImage(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return null; // Return null for no image, since it's optional
            }

            // Generate unique filename
            String fileName = UUID.randomUUID().toString() + "_" +
                    StringUtils.cleanPath(file.getOriginalFilename());

            // Resolve the file path
            Path targetLocation = Paths.get(employeeImagesDir).resolve(fileName);

            // Copy the file
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store employee image", ex);
        }
    }

    // Original method for deleting logos
    public void deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(logoUploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Error deleting file", ex);
        }
    }

    // New method for deleting employee images
    public void deleteEmployeeImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        try {
            Path filePath = Paths.get(employeeImagesDir).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Error deleting employee image", ex);
        }
    }
    // New method for storing product images
    public String storeProductImage(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return null;
            }

            // Generate unique filename
            String fileName = UUID.randomUUID().toString() + "_" +
                    StringUtils.cleanPath(file.getOriginalFilename());

            // Resolve the file path
            Path targetLocation = Paths.get(productImagesDir).resolve(fileName);

            // Copy the file
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store product image", ex);
        }
    }

    // New method for deleting product images
    public void deleteProductImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        try {
            Path filePath = Paths.get(productImagesDir).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Error deleting product image", ex);
        }
    }

    // Method to get product image URL
    public String getProductImageUrl(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        return "/uploads/products/" + fileName;
    }

}