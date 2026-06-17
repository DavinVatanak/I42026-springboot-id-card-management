package net.orderzone.idcard.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ID Card Management System API")
                        .version("1.0.0")
                        .description("""
                                REST API for managing ID cards for Students, Employees, and Users.
                                
                                **Highlights:**
                                - Profile CRUD with UUID + auto-generated registration numbers
                                - Photo upload (JPEG/PNG ≤ 5 MB)
                                - Template theming via primaryColor / secondaryColor
                                - Live HTML preview & PDF export (QR + barcode)
                                - Batch PDF download as ZIP
                                """)
                        .contact(new Contact().name("ID Card Team").email("admin@idcard.local")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Local")));
    }
}
