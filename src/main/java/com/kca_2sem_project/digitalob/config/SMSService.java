package com.kca_2sem_project.digitalob.config;

import com.africastalking.AfricasTalking;
import com.africastalking.SmsService;
import com.africastalking.sms.Recipient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SMSService {

    private static final Logger logger = LoggerFactory.getLogger(SMSService.class);

    @Value("${africas.talking.username}")
    private String username;

    @Value("${africas.talking.api.key}")
    private String apiKey;

    @Value("${africas.talking.sender.id:}")
    private String senderId;

    @Value("${africas.talking.environment:sandbox}")
    private String environment;

    private SmsService smsService;

    @PostConstruct
    public void initialize() {
        try {
            logger.info("=== SMS SERVICE INITIALIZATION ===");
            logger.info("Username: {}", username);
            logger.info("API Key: {}...", apiKey != null ? apiKey.substring(0, Math.min(10, apiKey.length())) : "NULL");
            logger.info("Sender ID: '{}'", senderId);
            logger.info("Environment: {}", environment);

            // Initialize Africa's Talking
            AfricasTalking.initialize(username, apiKey);
            logger.info("AfricasTalking initialized successfully");

            // Get SMS service
            this.smsService = AfricasTalking.getService(AfricasTalking.SERVICE_SMS);
            logger.info("SMS service obtained successfully");

            if (this.smsService == null) {
                logger.error("SMS SERVICE IS NULL AFTER INITIALIZATION!");
                System.err.println("SMS SERVICE IS NULL AFTER INITIALIZATION!");
            } else {
                logger.info("SMS service initialized successfully");
                System.out.println("SMS SERVICE INITIALIZED SUCCESSFULLY");
            }

            logger.info("=====================================");

        } catch (Exception e) {
            logger.error("=== SMS INITIALIZATION ERROR ===");
            logger.error("Failed to initialize SMS service: {}", e.getMessage());
            logger.error("Full stack trace:", e);
            System.err.println("SMS INITIALIZATION ERROR: " + e.getMessage());
            e.printStackTrace();
            System.err.println("===============================");
        }
    }

    /**
     * Send SMS notification for case creation
     */
    public boolean sendCaseCreationSMS(String phoneNumber, String reporterName, Long caseId) {
        try {
            logger.info("=== SMS SERVICE DEBUG ===");
            logger.info("Attempting to send case creation SMS");
            logger.info("Phone: {}", phoneNumber);
            logger.info("Reporter: {}", reporterName);
            logger.info("Case ID: {}", caseId);
            logger.info("Environment: {}", environment);
            logger.info("Username: {}", username);
            logger.info("Sender ID: {}", senderId);

            // Format the message
            String message = String.format(
                    "Dear %s, Case OB Number %d has been booked at Ruaraka Police Station. " +
                            "We will keep you updated on the progress. Thank you.",
                    reporterName, caseId
            );

            logger.info("Message: {}", message);
            logger.info("========================");

            return sendSMS(phoneNumber, message);
        } catch (Exception e) {
            logger.error("=== SMS CREATION ERROR ===");
            logger.error("Error sending case creation SMS to {}: {}", phoneNumber, e.getMessage());
            logger.error("Full stack trace:", e);
            System.err.println("SMS CREATION ERROR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Send custom SMS
     */
    public boolean sendCustomSms(String phoneNumber, String message) {
        logger.info("=== CUSTOM SMS DEBUG ===");
        logger.info("Phone: {}", phoneNumber);
        logger.info("Message: {}", message);
        logger.info("=======================");
        return sendSMS(phoneNumber, message);
    }

    /**
     * Core SMS sending method
     */
    private boolean sendSMS(String phoneNumber, String message) {
        try {
            logger.info("=== CORE SMS SENDING ===");
            logger.info("Original phone: {}", phoneNumber);

            // Validate phone number format (should start with +254 for Kenya)
            String formattedPhone = formatPhoneNumber(phoneNumber);
            logger.info("Formatted phone: {}", formattedPhone);

            if (formattedPhone == null) {
                logger.error("PHONE FORMAT ERROR: Invalid phone number format: {}", phoneNumber);
                System.err.println("PHONE FORMAT ERROR: Invalid phone number format: " + phoneNumber);
                return false;
            }

            // Check if SMS service is initialized
            if (smsService == null) {
                logger.error("SMS SERVICE ERROR: SMS service is not initialized!");
                System.err.println("SMS SERVICE ERROR: SMS service is not initialized!");
                return false;
            }

            logger.info("Sending SMS with:");
            logger.info("- Message: {}", message);
            logger.info("- Sender ID: '{}'", senderId);
            logger.info("- Phone: {}", formattedPhone);
            logger.info("- Message length: {}", message.length());

            // Send SMS
            List<Recipient> response;
            if (senderId != null && !senderId.trim().isEmpty()) {
                logger.info("Sending with custom sender ID: {}", senderId);
                response = smsService.send(message, senderId, new String[]{formattedPhone}, true);
            } else {
                logger.info("Sending with default sender ID");
                response = smsService.send(message, new String[]{formattedPhone}, true);
            }

            logger.info("SMS API Response received");

            // Check response
            if (response != null && !response.isEmpty()) {
                logger.info("Response size: {}", response.size());

                for (int i = 0; i < response.size(); i++) {
                    Recipient recipient = response.get(i);
                    logger.info("Recipient {}: Status='{}', StatusCode='{}', Number='{}'",
                            i, recipient.status, recipient.statusCode, recipient.number);
                    System.out.println("SMS RESPONSE " + i + ": Status=" + recipient.status +
                            ", StatusCode=" + recipient.statusCode + ", Number=" + recipient.number);
                }

                Recipient recipient = response.get(0);
                boolean success = "Success".equalsIgnoreCase(recipient.status) ||
                        "Queued".equalsIgnoreCase(recipient.status) ||
                        "Sent".equalsIgnoreCase(recipient.status);

                logger.info("SMS Result: {}", success ? "SUCCESS" : "FAILED");
                System.out.println("SMS FINAL RESULT: " + (success ? "SUCCESS" : "FAILED"));

                return success;
            } else {
                logger.error("SMS ERROR: Empty or null response from SMS service");
                System.err.println("SMS ERROR: Empty or null response from SMS service");
                return false;
            }

        } catch (Exception e) {
            logger.error("=== SMS SENDING EXCEPTION ===");
            logger.error("Exception type: {}", e.getClass().getSimpleName());
            logger.error("Exception message: {}", e.getMessage());
            logger.error("Full stack trace:", e);

            System.err.println("=== SMS SENDING EXCEPTION ===");
            System.err.println("Exception type: " + e.getClass().getSimpleName());
            System.err.println("Exception message: " + e.getMessage());
            System.err.println("Phone: " + phoneNumber);
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Format phone number to international format
     */
    private String formatPhoneNumber(String phoneNumber) {
        try {
            logger.info("=== PHONE FORMATTING ===");
            logger.info("Input phone: '{}'", phoneNumber);

            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                logger.error("Phone number is null or empty");
                return null;
            }

            // Remove all non-numeric characters except +
            String cleaned = phoneNumber.replaceAll("[^+\\d]", "");
            logger.info("Cleaned phone: '{}'", cleaned);

            String result;
            // Handle different Kenyan number formats
            if (cleaned.startsWith("+254")) {
                result = cleaned; // Already in international format
                logger.info("Already international format");
            } else if (cleaned.startsWith("254")) {
                result = "+" + cleaned; // Add + sign
                logger.info("Added + sign");
            } else if (cleaned.startsWith("0") && cleaned.length() == 10) {
                result = "+254" + cleaned.substring(1); // Replace 0 with +254
                logger.info("Replaced 0 with +254");
            } else if (cleaned.length() == 9) {
                result = "+254" + cleaned; // Add country code
                logger.info("Added country code");
            } else {
                logger.error("Invalid phone format - Length: {}, Content: '{}'", cleaned.length(), cleaned);
                return null; // Invalid format
            }

            logger.info("Final formatted phone: '{}'", result);
            logger.info("=======================");
            return result;

        } catch (Exception e) {
            logger.error("Error formatting phone number '{}': {}", phoneNumber, e.getMessage());
            logger.error("Stack trace:", e);
            return null;
        }
    }
}