package com.kca_2sem_project.digitalob.licencemanagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
    Optional<License> findFirstByOrderByCreatedDatetimeDesc();
}
