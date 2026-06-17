package com.idcard.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * JPA Entity representing an ID Card Template.
 * Stores HTML/CSS layout used to render ID cards.
 */
@Entity
@Table(name = "templates",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Raw HTML template string with Thymeleaf variable placeholders.
     * e.g., [[${profile.fullName}]]
     */
    @Column(name = "html_template", columnDefinition = "TEXT")
    private String htmlTemplate;

    @Column(name = "css_style", columnDefinition = "TEXT")
    private String cssStyle;

    @Enumerated(EnumType.STRING)
    @Column(name = "barcode_type", length = 20)
    @Builder.Default
    private BarcodeType barcodeType = BarcodeType.CODE_128;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
