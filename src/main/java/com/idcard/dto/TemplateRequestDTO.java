package com.idcard.dto;

import com.idcard.model.BarcodeType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO for creating or updating a Template.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateRequestDTO {

    @NotBlank(message = "Template name is required")
    private String name;

    private String htmlTemplate;

    private String cssStyle;

    @Builder.Default
    private BarcodeType barcodeType = BarcodeType.CODE_128;

    @Builder.Default
    private Boolean active = true;
}
