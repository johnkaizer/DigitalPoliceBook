package com.kca_2sem_project.digitalob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude =  {SecurityAutoConfiguration.class})
@EnableScheduling
public class DigitalObApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalObApplication.class, args);
    }

}
