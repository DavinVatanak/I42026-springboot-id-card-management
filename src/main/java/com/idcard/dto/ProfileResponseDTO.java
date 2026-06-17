package com.idcard.dto;

import com.idcard.model.ProfileType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for returning Profile data to clients.
 * Never exposes the JPA entity directly.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private String photoPath;
    private String registrationNumber;
    private ProfileType profileType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
