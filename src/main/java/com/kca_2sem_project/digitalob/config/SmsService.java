package com.kca_2sem_project.digitalob.config;


import com.africastalking.AfricasTalking;
import com.africastalking.sms.Recipient;
import com.kca_2sem_project.digitalob.casesmanagement.Case;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SmsService {

    @Value("${africas.talking.sender.id:RUARAKA_POLICE}")
    private String senderId;

    /**
     * Format phone number to include +254 prefix
     * @param phoneNumber the original phone number
     * @return formatted phone number with +254 prefix
     */
    public String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }

        // Remove any spaces, hyphens, or other non-digit characters
        String cleanNumber = phoneNumber.replaceAll("[^0-9]", "");

        // Handle different formats
        if (cleanNumber.startsWith("254")) {
            // Already has country code, just add +
            return "+" + cleanNumber;
        } else if (cleanNumber.startsWith("0") && cleanNumber.length() == 10) {
            // Format: 0712345678 -> +254712345678
            return "+254" + cleanNumber.substring(1);
        } else if (cleanNumber.length() == 9) {
            // Format: 712345678 -> +254712345678
            return "+254" + cleanNumber;
        } else {
            // Invalid format, return as is with + prefix
            log.warn("Invalid phone number format: {}", phoneNumber);
            return "+" + cleanNumber;
        }
    }

    /**
     * Send SMS notification for new case registration
     * @param caseEntity the newly created case
     * @return true if SMS sent successfully, false otherwise
     */
    public boolean sendCaseRegistrationSms(Case caseEntity) {
        try {
            String formattedPhone = formatPhoneNumber(caseEntity.getReporterPhone());

            if (formattedPhone == null) {
                log.error("Invalid phone number for case ID: {}", caseEntity.getId());
                return false;
            }

            String message = buildCaseRegistrationMessage(caseEntity);

            // Get SMS service from Africa's Talking
            com.africastalking.SmsService smsService = AfricasTalking.getService(AfricasTalking.SERVICE_SMS);

            // Create recipients list
            List<Recipient> recipients = new ArrayList<>();
            recipients.add(new Recipient());

            // Send SMS
            List<com.africastalking.sms.SmsMessage> response = smsService.send(message, senderId, recipients, true);

            if (response != null && !response.isEmpty()) {
                com.africastalking.sms.SmsMessage smsMessage = response.get(0);
                if ("Success".equalsIgnoreCase(smsMessage.status)) {
                    log.info("SMS sent successfully for case ID: {} to {}", caseEntity.getId(), formattedPhone);
                    return true;
                } else {
                    log.error("SMS failed for case ID: {}. Status: {}",
                            caseEntity.getId(), smsMessage.status);
                    return false;
                }
            } else {
                log.error("No response data received for case ID: {}", caseEntity.getId());
                return false;
            }

        } catch (Exception e) {
            log.error("Error sending SMS for case ID: {}", caseEntity.getId(), e);
            return false;
        }
    }

    /**
     * Build the SMS message content for case registration
     * @param caseEntity the case details
     * @return formatted SMS message
     */
    private String buildCaseRegistrationMessage(Case caseEntity) {
        return String.format(
                "Dear %s, your case has been successfully registered at Ruaraka Police Station. " +
                        "Your OB Number is: %d. Case Type: %s. Location: %s. " +
                        "You will be contacted for further updates. Thank you.",
                caseEntity.getReporterName(),
                caseEntity.getId(),
                caseEntity.getCaseType(),
                caseEntity.getCrimeLocation()
        );
    }

    /**
     * Send SMS with custom message
     * @param phoneNumber recipient phone number
     * @param message SMS content
     * @return true if sent successfully
     */
    public boolean sendCustomSms(String phoneNumber, String message) {
        try {
            String formattedPhone = formatPhoneNumber(phoneNumber);

            if (formattedPhone == null) {
                log.error("Invalid phone number: {}", phoneNumber);
                return false;
            }

            // Get SMS service from Africa's Talking
            com.africastalking.SmsService smsService = AfricasTalking.getService(AfricasTalking.SERVICE_SMS);

            // Create recipients list
            List<Recipient> recipients = new ArrayList<>();
            recipients.add(new Recipient(formattedPhone, null));

            // Send SMS
            List<com.africastalking.sms.SmsMessage> response = smsService.send(message, senderId, recipients, true);

            if (response != null && !response.isEmpty()) {
                com.africastalking.sms.SmsMessage smsMessage = response.get(0);
                return "Success".equalsIgnoreCase(smsMessage.status);
            }

            return false;

        } catch (Exception e) {
            log.error("Error sending custom SMS to: {}", phoneNumber, e);
            return false;
        }
    }
}