package com.idcard.dto;

import com.idcard.model.ProfileType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO for creating or updating a Profile.
 * Carries validated input data from the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequestDTO {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must be at most 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must be at most 150 characters")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9\\-\\s]{6,20}$", message = "Phone number is invalid")
    private String phone;

    private String address;

    private LocalDate dateOfBirth;

    @NotNull(message = "Profile type is required")
    private ProfileType profileType;
}
