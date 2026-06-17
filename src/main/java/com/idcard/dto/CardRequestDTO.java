package com.idcard.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * DTO for card preview and batch PDF generation requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardRequestDTO {

    @NotNull(message = "Profile ID is required")
    private Long profileId;

    @NotNull(message = "Template ID is required")
    private Long templateId;
}
