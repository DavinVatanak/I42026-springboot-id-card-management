package com.idcard.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI / Swagger 3 configuration.
 * UI available at: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenAPIConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI openAPI() {
        Server localServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Local Development Server");

        Contact contact = new Contact()
                .name("ID Card Management Team")
                .email("admin@idcard.local")
                .url("https://id-system.local");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("ID Card Management System API")
                .version("1.0.0")
                .description("""
                        Production-grade REST API for managing ID cards for Students, Employees, and Users.
                        
                        **Features:**
                        - Profile CRUD with automatic registration number generation
                        - Photo upload (JPEG/PNG, max 5MB)
                        - ID card template management
                        - Live HTML preview generation
                        - PDF export with QR code and barcode
                        - Batch PDF generation as ZIP archive
                        """)
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
