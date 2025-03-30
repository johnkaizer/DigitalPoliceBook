package com.kca_2sem_project.digitalob.officers;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Officer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String idNumber;
    private String officerNumber;
    private String gender;
    private String email;
    private String username;
    private String password;
    private String status; // e.g., ACTIVE, INACTIVE, etc.

}
