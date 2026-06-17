package net.orderzone.idcard.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

/**
 * DTO for batch ID card generation requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchCardRequestDTO {

    @NotEmpty(message = "Profile IDs list must not be empty")
    private List<Long> profileIds;

    @lombok.NonNull
    private Long templateId;
}
