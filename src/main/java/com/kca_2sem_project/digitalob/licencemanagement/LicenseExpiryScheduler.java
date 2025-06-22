package com.kca_2sem_project.digitalob.licencemanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LicenseExpiryScheduler {

    @Autowired
    private LicenseService licenseService;

    @Scheduled(cron = "0 0 14 * * ?") // Runs daily at 2 PM
    public void runExpiryCheck() {
        licenseService.checkAndExpireLicenses();
    }
}
