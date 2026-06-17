package net.orderzone.idcard.dto;

import net.orderzone.idcard.model.BarcodeType;
import net.orderzone.idcard.model.ProfileType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for returning Profile data to the client.
 * Never exposes the JPA entity directly.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDTO {

    private Long id;
    private String uuid;
    private String registrationNumber;
    private ProfileType type;
    private String fullName;
    private String department;
    private String title;
    private String email;
    private String phone;
    private String bloodGroup;
    private LocalDate dateOfBirth;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String photoFileName;
    private String photoContentType;
    private BarcodeType barcodeType;

    /** Embedded template summary (not the full entity). */
    private TemplateResponseDTO template;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
