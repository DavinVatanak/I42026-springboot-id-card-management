package net.orderzone.idcard.dto;

import lombok.*;

/**
 * DTO for returning Template data to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateResponseDTO {

    private Long id;
    private String code;
    private String name;
    private String organizationName;
    private String layout;
    private String primaryColor;
    private String secondaryColor;
    private String textColor;
    private String tagline;
}
