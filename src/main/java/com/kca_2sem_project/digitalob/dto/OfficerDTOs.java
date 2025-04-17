package com.kca_2sem_project.digitalob.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class OfficerDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private Long id;
        private String name;
        private String role;
        private String status;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfficerResponse {
        private Long id;
        private String name;
        private String idNumber;
        private String officerNumber;
        private String role;
        private String gender;
        private String specialization;
        private String email;
        private String username;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse {
        private String message;
        private boolean success;
    }
}