package com.idcard.dto;

import com.idcard.model.BarcodeType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for returning Template data to clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateResponseDTO {

    private Long id;
    private String name;
    private String htmlTemplate;
    private String cssStyle;
    private BarcodeType barcodeType;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
