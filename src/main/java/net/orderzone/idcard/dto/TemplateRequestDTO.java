package net.orderzone.idcard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for creating or updating a Template.
 * Reflects the Template entity: code, name, organizationName, layout, colours, tagline.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateRequestDTO {

    @NotBlank(message = "Template code is required")
    @Size(max = 60)
    private String code;

    @NotBlank(message = "Template name is required")
    @Size(max = 80)
    private String name;

    @Size(max = 120)
    private String organizationName;

    /** VERTICAL or HORIZONTAL */
    @Builder.Default
    private String layout = "VERTICAL";

    /** Primary brand colour as hex, e.g. #1d4ed8. */
    @Builder.Default
    private String primaryColor = "#1d4ed8";

    @Builder.Default
    private String secondaryColor = "#e0e7ff";

    @Builder.Default
    private String textColor = "#111827";

    @Size(max = 255)
    private String tagline;
}
