package com.kca_2sem_project.digitalob.config;

import com.africastalking.AfricasTalking;
import com.africastalking.SmsService;
import com.africastalking.sms.Recipient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.ArrayList;

// 2. SMS Configuration Class
@Configuration
public class SmsConfig {

    @Value("${africas.talking.username}")
    private String username;

    @Value("${africas.talking.api.key}")
    private String apiKey;

    @Bean
    public void initializeAfricasTalking() {
        AfricasTalking.initialize(username, apiKey);
    }
}