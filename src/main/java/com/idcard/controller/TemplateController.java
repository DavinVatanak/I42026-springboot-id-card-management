package com.idcard.controller;

import com.idcard.dto.TemplateRequestDTO;
import com.idcard.dto.TemplateResponseDTO;
import com.idcard.service.CardService;
import com.idcard.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Template management and preview.
 * Base path: /api/templates
 */
@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Tag(name = "Template", description = "ID Card Template Management APIs")
public class TemplateController {

    private final TemplateService templateService;
    private final CardService cardService;

    @PostMapping
    @Operation(summary = "Create a new template")
    public ResponseEntity<TemplateResponseDTO> createTemplate(
            @Valid @RequestBody TemplateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(templateService.createTemplate(request));
    }

    @GetMapping
    @Operation(summary = "Get all templates")
    public ResponseEntity<List<TemplateResponseDTO>> getAllTemplates(
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        List<TemplateResponseDTO> result = activeOnly
                ? templateService.getActiveTemplates()
                : templateService.getAllTemplates();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get template by ID")
    public ResponseEntity<TemplateResponseDTO> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getTemplateById(id));
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
     * Preview endpoint: renders the ID card HTML for a specific profile using this template.
     * GET /api/templates/{templateId}/preview/{profileId}
     */
    @GetMapping(value = "/{templateId}/preview/{profileId}",
                produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "Preview ID card HTML for a profile + template combination")
    public ResponseEntity<String> previewCard(
            @PathVariable Long templateId,
            @PathVariable Long profileId) {

        com.idcard.dto.CardRequestDTO req = com.idcard.dto.CardRequestDTO.builder()
                .profileId(profileId)
                .templateId(templateId)
                .build();
        String html = cardService.generatePreviewHtml(req);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }
}
