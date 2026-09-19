package com.hms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Entry point for the Hospital Management System backend.
 * Bootstraps Spring Boot, component scanning across com.hms.*
 */
@SpringBootApplication
@EnableAsync
public class HospitalManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(HospitalManagementSystemApplication.class, args);
    }
}
