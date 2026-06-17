package com.idcard.service.impl;

import com.idcard.dto.CardRequestDTO;
import com.idcard.exception.ResourceNotFoundException;
import com.idcard.model.Profile;
import com.idcard.model.Template;
import com.idcard.repository.ProfileRepository;
import com.idcard.repository.TemplateRepository;
import com.idcard.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Implementation of CardService.
 * Orchestrates preview HTML rendering, PDF generation, and batch ZIP creation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final ProfileRepository profileRepository;
    private final TemplateRepository templateRepository;
    private final QRCodeService qrCodeService;
    private final BarcodeService barcodeService;
    private final PDFService pdfService;

    @Override
    public String generatePreviewHtml(CardRequestDTO request) {
        Profile profile = findProfileOrThrow(request.getProfileId());
        Template template = findTemplateOrThrow(request.getTemplateId());

        // Generate QR and barcode as Base64 for inline HTML embedding
        String qrBase64 = qrCodeService.generateQRCodeBase64(
                profile.getId(), profile.getRegistrationNumber());
        String barcodeBase64 = barcodeService.generateBarcodeBase64(
                profile.getRegistrationNumber(), template.getBarcodeType());

        // Build the HTML preview
        return buildPreviewHtml(profile, template, qrBase64, barcodeBase64);
    }

    @Override
    public byte[] generatePDF(Long profileId) {
        Profile profile = findProfileOrThrow(profileId);
        // Use the first active template as default
        Template template = templateRepository.findByActiveTrue()
                .stream().findFirst()
                .orElse(null);

        return pdfService.generateIDCardPDF(profile, template);
    }

    @Override
    public byte[] generateBatchZip(List<Long> profileIds, Long templateId) {
        log.info("Generating batch ZIP for {} profiles", profileIds.size());
        Template template = findTemplateOrThrow(templateId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (Long profileId : profileIds) {
                try {
                    Profile profile = findProfileOrThrow(profileId);
                    byte[] pdfBytes = pdfService.generateIDCardPDF(profile, template);

                    // Add each PDF as a named entry in the ZIP
                    String entryName = profile.getRegistrationNumber() + "_id_card.pdf";
                    ZipEntry entry = new ZipEntry(entryName);
                    zos.putNextEntry(entry);
                    zos.write(pdfBytes);
                    zos.closeEntry();

                    log.debug("Added {} to ZIP", entryName);
                } catch (ResourceNotFoundException e) {
                    log.warn("Profile {} not found during batch generation, skipping.", profileId);
                }
            }

            zos.finish();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Batch ZIP generation failed: {}", e.getMessage(), e);
            throw new RuntimeException("Batch PDF generation failed", e);
        }
    }

    // ─── HTML Preview Builder ─────────────────────────────────────────────────

    /**
     * Builds an inline HTML ID card preview string.
     * Embeds QR and barcode as Base64 data URIs so no external file access is needed.
     */
    private String buildPreviewHtml(Profile profile, Template template,
                                     String qrBase64, String barcodeBase64) {

        String customCss = template.getCssStyle() != null ? template.getCssStyle() : "";

        String photoTag = profile.getPhotoPath() != null
                ? "<img src='/" + profile.getPhotoPath() + "' alt='Photo' class='photo'>"
                : "<div class='photo-placeholder'>No Photo</div>";

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>ID Card Preview</title>
                    <style>
                        body { font-family: 'Segoe UI', Arial, sans-serif; display: flex;
                               justify-content: center; align-items: center; min-height: 100vh;
                               background: #f0f2f5; margin: 0; }
                        .id-card { width: 340px; border-radius: 12px; overflow: hidden;
                                   box-shadow: 0 8px 32px rgba(0,0,0,0.18); background: #fff; }
                        .card-header { background: #0f3460; color: #fff; text-align: center;
                                       padding: 14px 10px 10px; }
                        .card-header h2 { margin: 0; font-size: 18px; letter-spacing: 2px; }
                        .badge { background: #e53935; color: #fff; border-radius: 12px;
                                 padding: 2px 12px; font-size: 11px; display: inline-block; margin-top: 4px; }
                        .card-body { display: flex; padding: 16px; background: #f5f5f5; gap: 14px; }
                        .photo { width: 80px; height: 100px; object-fit: cover; border-radius: 6px;
                                 border: 2px solid #0f3460; }
                        .photo-placeholder { width: 80px; height: 100px; background: #ccc;
                                             border-radius: 6px; display: flex; align-items: center;
                                             justify-content: center; font-size: 11px; color: #666; }
                        .info { flex: 1; }
                        .info h3 { margin: 0 0 4px; font-size: 15px; color: #0f3460; }
                        .info p { margin: 2px 0; font-size: 12px; color: #444; }
                        .reg { font-size: 11px; color: #888; margin-top: 6px; }
                        .card-codes { display: flex; justify-content: space-around; align-items: center;
                                      padding: 10px; background: #f5f5f5; border-top: 1px solid #e0e0e0; }
                        .card-codes img { display: block; }
                        .code-label { font-size: 9px; color: #888; text-align: center; margin-top: 2px; }
                        .card-footer { background: #0f3460; color: rgba(255,255,255,0.7);
                                       font-size: 9px; text-align: center; padding: 6px; }
                        %s
                    </style>
                </head>
                <body>
                    <div class="id-card">
                        <div class="card-header">
                            <h2>ID CARD</h2>
                            <span class="badge">%s</span>
                        </div>
                        <div class="card-body">
                            %s
                            <div class="info">
                                <h3>%s</h3>
                                <p>%s</p>
                                <p>%s</p>
                                <p class="reg">Reg#: %s</p>
                            </div>
                        </div>
                        <div class="card-codes">
                            <div>
                                <img src="%s" width="80" height="80" alt="QR Code">
                                <div class="code-label">Scan to Verify</div>
                            </div>
                            <div>
                                <img src="%s" width="130" height="45" alt="Barcode">
                                <div class="code-label">%s</div>
                            </div>
                        </div>
                        <div class="card-footer">
                            This card is the property of the issuing organization.
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                customCss,
                profile.getProfileType().name(),
                photoTag,
                profile.getFullName(),
                profile.getEmail() != null ? profile.getEmail() : "",
                profile.getPhone() != null ? profile.getPhone() : "",
                profile.getRegistrationNumber(),
                qrBase64,
                barcodeBase64,
                profile.getRegistrationNumber()
        );
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private Profile findProfileOrThrow(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", id));
    }

    private Template findTemplateOrThrow(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template", "id", id));
    }
}
