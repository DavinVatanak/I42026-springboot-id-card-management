package net.orderzone.idcard.service.impl;

import net.orderzone.idcard.dto.CardRequestDTO;
import net.orderzone.idcard.exception.ResourceNotFoundException;
import net.orderzone.idcard.model.Profile;
import net.orderzone.idcard.model.Template;
import net.orderzone.idcard.repository.ProfileRepository;
import net.orderzone.idcard.repository.TemplateRepository;
import net.orderzone.idcard.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * CardService implementation — orchestrates HTML preview, PDF, and batch ZIP.
 * Uses the Template's colour fields for theming the card.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final ProfileRepository  profileRepository;
    private final TemplateRepository templateRepository;
    private final QRCodeService      qrCodeService;
    private final BarcodeService     barcodeService;
    private final PDFService         pdfService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // ─── Preview ──────────────────────────────────────────────────────────────

    @Override
    public String generatePreviewHtml(CardRequestDTO request) {
        Profile  profile  = findProfileOrThrow(request.getProfileId());
        Template template = findTemplateOrThrow(request.getTemplateId());

        String qrBase64      = qrCodeService.generateQRCodeBase64(
                profile.getUuid(), profile.getRegistrationNumber());
        String barcodeBase64 = barcodeService.generateBarcodeBase64(
                profile.getRegistrationNumber(), profile.getBarcodeType());

        return buildPreviewHtml(profile, template, qrBase64, barcodeBase64);
    }

    // ─── PDF ──────────────────────────────────────────────────────────────────

    @Override
    public byte[] generatePDF(Long profileId) {
        Profile profile = findProfileOrThrow(profileId);
        // Use the profile's linked template if available
        Template template = profile.getTemplate();
        return pdfService.generateIDCardPDF(profile, template);
    }

    // ─── Batch ZIP ────────────────────────────────────────────────────────────

    @Override
    public byte[] generateBatchZip(List<Long> profileIds, Long templateId) {
        log.info("Batch ZIP: {} profiles, templateId={}", profileIds.size(), templateId);
        Template template = findTemplateOrThrow(templateId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (Long profileId : profileIds) {
                try {
                    Profile profile  = findProfileOrThrow(profileId);
                    byte[]  pdfBytes = pdfService.generateIDCardPDF(profile, template);
                    String  entry    = profile.getRegistrationNumber() + "_id_card.pdf";
                    zos.putNextEntry(new ZipEntry(entry));
                    zos.write(pdfBytes);
                    zos.closeEntry();
                } catch (ResourceNotFoundException e) {
                    log.warn("Profile {} not found; skipping.", profileId);
                }
            }

            zos.finish();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Batch ZIP failed: {}", e.getMessage(), e);
            throw new RuntimeException("Batch PDF generation failed", e);
        }
    }

    // ─── HTML Builder ─────────────────────────────────────────────────────────

    private String buildPreviewHtml(Profile profile, Template template,
                                     String qrBase64, String barcodeBase64) {

        String primary   = template.getPrimaryColor()   != null ? template.getPrimaryColor()   : "#1d4ed8";
        String secondary = template.getSecondaryColor() != null ? template.getSecondaryColor() : "#e0e7ff";
        String textColor = template.getTextColor()      != null ? template.getTextColor()      : "#111827";
        String orgName   = template.getOrganizationName() != null ? template.getOrganizationName() : "ID Card System";

        String photoTag = profile.hasPhoto()
                ? "<img src='/uploads/" + profile.getPhotoFileName() + "' alt='Photo' class='photo'>"
                : "<div class='photo-placeholder'>No Photo</div>";

        String titleLine = profile.getTitle() != null
                ? "<p class='sub'>" + profile.getTitle() + "</p>" : "";
        String deptLine  = profile.getDepartment() != null
                ? "<p class='sub'>Dept: " + profile.getDepartment() + "</p>" : "";
        String emailLine = profile.getEmail() != null
                ? "<p>" + profile.getEmail() + "</p>" : "";
        String phoneLine = profile.getPhone() != null
                ? "<p>" + profile.getPhone() + "</p>" : "";
        String bloodLine = profile.getBloodGroup() != null
                ? "<p>Blood: " + profile.getBloodGroup() + "</p>" : "";
        String expLine   = profile.getExpiryDate() != null
                ? "<p>Exp: " + profile.getExpiryDate() + "</p>" : "";

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <title>ID Card Preview — %s</title>
                  <style>
                    body { font-family:'Segoe UI',Arial,sans-serif; display:flex; justify-content:center;
                           align-items:center; min-height:100vh; background:#f0f2f5; margin:0; }
                    .id-card { width:340px; border-radius:12px; overflow:hidden;
                               box-shadow:0 8px 32px rgba(0,0,0,.18); background:#fff; }
                    .card-header { background:%s; color:#fff; text-align:center; padding:12px 10px 8px; }
                    .card-header h2 { margin:0; font-size:13px; letter-spacing:1px; }
                    .badge { background:%s; color:%s; border-radius:12px; padding:2px 12px;
                             font-size:10px; display:inline-block; margin-top:4px; }
                    .card-body { display:flex; padding:14px; background:%s; gap:12px; }
                    .photo { width:78px; height:98px; object-fit:cover; border-radius:6px;
                             border:2px solid %s; }
                    .photo-placeholder { width:78px; height:98px; background:#ccc; border-radius:6px;
                                         display:flex; align-items:center; justify-content:center;
                                         font-size:11px; color:#666; }
                    .info { flex:1; color:%s; }
                    .info h3 { margin:0 0 3px; font-size:14px; color:%s; }
                    .info p  { margin:2px 0; font-size:11px; }
                    .info .sub { font-weight:600; font-size:11px; }
                    .reg { font-size:10px; color:#888; margin-top:5px; }
                    .card-codes { display:flex; justify-content:space-around; align-items:center;
                                  padding:8px; background:%s; border-top:1px solid #e0e0e0; }
                    .code-label { font-size:8px; color:#888; text-align:center; margin-top:2px; }
                    .card-footer { background:%s; color:rgba(255,255,255,.7);
                                   font-size:8px; text-align:center; padding:5px; }
                  </style>
                </head>
                <body>
                  <div class="id-card">
                    <div class="card-header">
                      <h2>%s</h2>
                      <span class="badge">%s</span>
                    </div>
                    <div class="card-body">
                      %s
                      <div class="info">
                        <h3>%s</h3>
                        %s%s%s%s%s%s
                        <div class="reg">Reg#: %s</div>
                      </div>
                    </div>
                    <div class="card-codes">
                      <div>
                        <img src="%s" width="78" height="78" alt="QR Code">
                        <div class="code-label">Scan to Verify</div>
                      </div>
                      <div>
                        <img src="%s" width="128" height="44" alt="Barcode">
                        <div class="code-label">%s</div>
                      </div>
                    </div>
                    <div class="card-footer">This card is property of %s</div>
                  </div>
                </body>
                </html>
                """.formatted(
                profile.getFullName(),
                primary, secondary, textColor, secondary, primary, textColor, primary,
                secondary, primary,
                orgName, profile.getType().name(),
                photoTag, profile.getFullName(),
                titleLine, deptLine, emailLine, phoneLine, bloodLine, expLine,
                profile.getRegistrationNumber(),
                qrBase64, barcodeBase64, profile.getRegistrationNumber(),
                orgName
        );
    }

    private Profile findProfileOrThrow(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", id));
    }

    private Template findTemplateOrThrow(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template", "id", id));
    }
}
