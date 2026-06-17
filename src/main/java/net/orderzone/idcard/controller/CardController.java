package net.orderzone.idcard.controller;

import net.orderzone.idcard.dto.BatchCardRequestDTO;
import net.orderzone.idcard.dto.CardRequestDTO;
import net.orderzone.idcard.service.CardService;
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
@Tag(name = "Card", description = "ID Card Generation (Preview, PDF, Batch)")
public class CardController {

    private final CardService cardService;

    @PostMapping(value = "/preview", produces = MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "Generate a live HTML ID card preview")
    public ResponseEntity<String> previewCard(@Valid @RequestBody CardRequestDTO request) {
        String html = cardService.generatePreviewHtml(request);
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    @GetMapping("/pdf/{profileId}")
    @Operation(summary = "Download a PDF ID card for a profile")
    public ResponseEntity<byte[]> downloadPDF(@PathVariable Long profileId) {
        log.info("PDF request for profile {}", profileId);
        byte[] pdf = cardService.generatePDF(profileId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("id_card_" + profileId + ".pdf").build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @PostMapping("/batch")
    @Operation(summary = "Generate batch PDF ID cards as a ZIP archive")
    public ResponseEntity<byte[]> generateBatch(@Valid @RequestBody BatchCardRequestDTO request) {
        log.info("Batch request: {} profiles", request.getProfileIds().size());
        byte[] zip = cardService.generateBatchZip(request.getProfileIds(), request.getTemplateId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("id_cards_batch.zip").build());
        return ResponseEntity.ok().headers(headers).body(zip);
    }
}
