package com.idcard.controller;

import com.idcard.dto.BatchCardRequestDTO;
import com.idcard.dto.CardRequestDTO;
import com.idcard.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for ID Card generation operations.
 * Base path: /api/cards
 */
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Card", description = "ID Card Generation APIs (Preview, PDF, Batch)")
public class CardController {

    private final CardService cardService;

    /**
     * Live preview of an ID card as rendered HTML.
     * POST /api/cards/preview
     */
    @PostMapping(value = "/preview", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "Generate a live HTML preview of an ID card")
    public ResponseEntity<String> previewCard(@Valid @RequestBody CardRequestDTO request) {
        log.info("POST /api/cards/preview - profileId: {}, templateId: {}",
                request.getProfileId(), request.getTemplateId());
        String html = cardService.generatePreviewHtml(request);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    /**
     * Generate and download a PDF for a single profile.
     * GET /api/cards/pdf/{profileId}
     */
    @GetMapping("/pdf/{profileId}")
    @Operation(summary = "Download a PDF ID card for a profile")
    public ResponseEntity<byte[]> downloadPDF(@PathVariable Long profileId) {
        log.info("GET /api/cards/pdf/{}", profileId);
        byte[] pdfBytes = cardService.generatePDF(profileId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("id_card_" + profileId + ".pdf")
                        .build()
        );
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    /**
     * Generate multiple ID card PDFs and return as a ZIP archive.
     * POST /api/cards/batch
     */
    @PostMapping("/batch")
    @Operation(summary = "Generate batch ID card PDFs and download as ZIP",
               description = "Accepts a list of profile IDs and a template ID. Returns a ZIP archive.")
    public ResponseEntity<byte[]> generateBatch(@Valid @RequestBody BatchCardRequestDTO request) {
        log.info("POST /api/cards/batch - {} profiles", request.getProfileIds().size());
        byte[] zipBytes = cardService.generateBatchZip(request.getProfileIds(), request.getTemplateId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("id_cards_batch.zip")
                        .build()
        );
        return ResponseEntity.ok().headers(headers).body(zipBytes);
    }
}
