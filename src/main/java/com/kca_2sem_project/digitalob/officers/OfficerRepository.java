package com.kca_2sem_project.digitalob.officers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfficerRepository extends JpaRepository<Officer, Long> {
    Optional<Officer> findByUsername(String username);
    Optional<Officer> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByIdNumber(String idNumber);
    boolean existsByOfficerNumber(String officerNumber);
}
