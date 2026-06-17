package net.orderzone.idcard.controller;

import net.orderzone.idcard.dto.CardRequestDTO;
import net.orderzone.idcard.dto.TemplateRequestDTO;
import net.orderzone.idcard.dto.TemplateResponseDTO;
import net.orderzone.idcard.service.CardService;
import net.orderzone.idcard.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Template management and HTML preview.
 * Base path: /api/templates
 */
@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Tag(name = "Template", description = "ID Card Template (Theme) Management")
public class TemplateController {

    private final TemplateService templateService;
    private final CardService     cardService;

    @PostMapping
    @Operation(summary = "Create a new template")
    public ResponseEntity<TemplateResponseDTO> createTemplate(
            @Valid @RequestBody TemplateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(templateService.createTemplate(request));
    }

    @GetMapping
    @Operation(summary = "List all templates")
    public ResponseEntity<List<TemplateResponseDTO>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get template by ID")
    public ResponseEntity<TemplateResponseDTO> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getTemplateById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get template by unique code")
    public ResponseEntity<TemplateResponseDTO> getTemplateByCode(@PathVariable String code) {
        return ResponseEntity.ok(templateService.getTemplateByCode(code));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a template")
    public ResponseEntity<TemplateResponseDTO> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody TemplateRequestDTO request) {
        return ResponseEntity.ok(templateService.updateTemplate(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a template")
    public ResponseEntity<Map<String, String>> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.ok(Map.of("message", "Template deleted successfully"));
    }

    /**
     * GET /api/templates/{templateId}/preview/{profileId}
     * Returns a rendered HTML ID card using the given template's colours.
     */
    @GetMapping(value = "/{templateId}/preview/{profileId}",
                produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "Preview ID card HTML for a profile using this template")
    public ResponseEntity<String> previewCard(
            @PathVariable Long templateId,
            @PathVariable Long profileId) {

        String html = cardService.generatePreviewHtml(
                CardRequestDTO.builder().profileId(profileId).templateId(templateId).build());
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }
}
