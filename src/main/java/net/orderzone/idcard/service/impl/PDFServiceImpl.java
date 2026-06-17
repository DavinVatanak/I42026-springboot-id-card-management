package net.orderzone.idcard.service.impl;

import net.orderzone.idcard.model.BarcodeType;
import net.orderzone.idcard.model.Profile;
import net.orderzone.idcard.model.Template;
import net.orderzone.idcard.service.BarcodeService;
import net.orderzone.idcard.service.PDFService;
import net.orderzone.idcard.service.QRCodeService;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * PDF generation using iText 8.
 * Uses the Template's primaryColor / secondaryColor / organizationName
 * to drive the card's visual theme.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PDFServiceImpl implements PDFService {

    private final QRCodeService  qrCodeService;
    private final BarcodeService barcodeService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public byte[] generateIDCardPDF(Profile profile, Template template) {
        log.info("Generating PDF for profile id={}", profile.getId());

        // Parse template colours (or fall back to navy defaults)
        DeviceRgb primaryRgb   = hexToRgb(template != null ? template.getPrimaryColor()   : "#0f3460");
        DeviceRgb secondaryRgb = hexToRgb(template != null ? template.getSecondaryColor() : "#e0e7ff");
        String orgName = (template != null && template.getOrganizationName() != null)
                ? template.getOrganizationName() : "ID Card System";

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter   writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            PageSize    card   = new PageSize(340, 215);   // credit-card size (scaled)
            Document    doc    = new Document(pdfDoc, card);
            doc.setMargins(0, 0, 0, 0);

            // ── Header ────────────────────────────────────────────────────────
            Table header = new Table(UnitValue.createPercentArray(new float[]{1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBackgroundColor(primaryRgb);

            Cell hCell = new Cell()
                    .add(new Paragraph(orgName)
                            .setFontColor(ColorConstants.WHITE).setFontSize(10).setBold()
                            .setTextAlignment(TextAlignment.CENTER))
                    .add(new Paragraph("ID CARD — " + profile.getType().name())
                            .setFontColor(secondaryRgb).setFontSize(8)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                    .setPaddingTop(8).setPaddingBottom(6);
            header.addCell(hCell);
            doc.add(header);

            // ── Body: photo + info ────────────────────────────────────────────
            Table body = new Table(UnitValue.createPercentArray(new float[]{2, 3}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBackgroundColor(secondaryRgb)
                    .setPadding(8);

            // Photo cell
            Cell photoCell = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(4);
            if (profile.hasPhoto()) {
                try {
                    byte[] photoBytes = Files.readAllBytes(
                            Paths.get(uploadDir).resolve(profile.getPhotoFileName()));
                    Image photo = new Image(ImageDataFactory.create(photoBytes))
                            .setWidth(72).setHeight(90)
                            .setBorder(new SolidBorder(primaryRgb, 2));
                    photoCell.add(photo);
                } catch (IOException e) {
                    log.warn("Photo not readable for profile {}: {}", profile.getId(), e.getMessage());
                    photoCell.add(new Paragraph("No Photo").setFontSize(8).setItalic()
                            .setFontColor(ColorConstants.GRAY));
                }
            } else {
                photoCell.add(new Paragraph("No Photo").setFontSize(8).setItalic()
                        .setFontColor(ColorConstants.GRAY));
            }
            body.addCell(photoCell);

            // Info cell
            Cell info = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                    .setPaddingLeft(8).setPaddingTop(4);
            info.add(new Paragraph(profile.getFullName()).setFontSize(11).setBold()
                    .setFontColor(primaryRgb));
            if (profile.getTitle() != null)
                info.add(new Paragraph(profile.getTitle()).setFontSize(8.5f));
            if (profile.getDepartment() != null)
                info.add(new Paragraph("Dept: " + profile.getDepartment()).setFontSize(8));
            if (profile.getEmail() != null)
                info.add(new Paragraph("Email: " + profile.getEmail()).setFontSize(7.5f));
            if (profile.getPhone() != null)
                info.add(new Paragraph("Phone: " + profile.getPhone()).setFontSize(7.5f));
            if (profile.getBloodGroup() != null)
                info.add(new Paragraph("Blood: " + profile.getBloodGroup()).setFontSize(7.5f));
            if (profile.getExpiryDate() != null)
                info.add(new Paragraph("Exp: " + profile.getExpiryDate()).setFontSize(7.5f));
            info.add(new Paragraph("Reg#: " + profile.getRegistrationNumber())
                    .setFontSize(7).setFontColor(ColorConstants.DARK_GRAY));
            body.addCell(info);
            doc.add(body);

            // ── QR + Barcode row ──────────────────────────────────────────────
            Table codes = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBackgroundColor(secondaryRgb).setPadding(6);

            // QR Code (uses uuid for stable public URL)
            Cell qrCell = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER);
            try {
                byte[] qrBytes = qrCodeService.generateQRCodeBytes(
                        profile.getUuid(), profile.getRegistrationNumber());
                qrCell.add(new Image(ImageDataFactory.create(qrBytes))
                        .setWidth(52).setHeight(52).setHorizontalAlignment(HorizontalAlignment.CENTER));
                qrCell.add(new Paragraph("Verify").setFontSize(6)
                        .setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.GRAY));
            } catch (Exception e) {
                log.warn("QR skipped: {}", e.getMessage());
            }
            codes.addCell(qrCell);

            // Barcode
            Cell bcCell = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER);
            try {
                BarcodeType barcodeType = profile.getBarcodeType() != null
                        ? profile.getBarcodeType() : BarcodeType.CODE_128;
                byte[] bcBytes = barcodeService.generateBarcodeBytes(
                        profile.getRegistrationNumber(), barcodeType);
                bcCell.add(new Image(ImageDataFactory.create(bcBytes))
                        .setWidth(100).setHeight(38).setHorizontalAlignment(HorizontalAlignment.CENTER));
                bcCell.add(new Paragraph(profile.getRegistrationNumber()).setFontSize(6)
                        .setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.GRAY));
            } catch (Exception e) {
                log.warn("Barcode skipped: {}", e.getMessage());
            }
            codes.addCell(bcCell);
            doc.add(codes);

            // ── Footer ────────────────────────────────────────────────────────
            Table footer = new Table(UnitValue.createPercentArray(new float[]{1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBackgroundColor(primaryRgb);
            footer.addCell(new Cell()
                    .add(new Paragraph("This card is the property of " + orgName)
                            .setFontColor(ColorConstants.WHITE).setFontSize(6)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                    .setPaddingTop(4).setPaddingBottom(4));
            doc.add(footer);

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("PDF generation failed: {}", e.getMessage(), e);
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    /**
     * Converts a hex colour string (e.g. "#1d4ed8") to an iText DeviceRgb.
     */
    private DeviceRgb hexToRgb(String hex) {
        try {
            String clean = hex.replace("#", "");
            int r = Integer.parseInt(clean.substring(0, 2), 16);
            int g = Integer.parseInt(clean.substring(2, 4), 16);
            int b = Integer.parseInt(clean.substring(4, 6), 16);
            return new DeviceRgb(r, g, b);
        } catch (Exception e) {
            return new DeviceRgb(15, 52, 96); // default navy
        }
    }
}
