package net.orderzone.idcard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.orderzone.idcard.model.BarcodeType;
import net.orderzone.idcard.model.ProfileType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO for creating or updating a Profile.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequestDTO {

    @NotNull(message = "Profile type is required")
    private ProfileType type;

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must be at most 120 characters")
    private String fullName;

    @Size(max = 80)
    private String department;

    /** Job title (employee) or program / class (student). */
    @Size(max = 120)
    private String title;

    @Email(message = "Email must be valid")
    @Size(max = 120)
    private String email;

    @NotBlank(message = "Phone is required")
    @Size(max = 40)
    private String phone;

    @Size(max = 60)
    private String bloodGroup;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issueDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;

    /** ID of the Template to associate with this profile. */
    private Long templateId;

    @Builder.Default
    private BarcodeType barcodeType = BarcodeType.CODE_128;
}
