package com.idcard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main entry point for the ID Card Management System.
 * Enables JPA Auditing for automatic createdAt/updatedAt timestamps.
 */
@SpringBootApplication
@EnableJpaAuditing
public class IdCardManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdCardManagementApplication.class, args);
    }
}
