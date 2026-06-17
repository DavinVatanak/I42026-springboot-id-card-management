package com.idcard.service.impl;

import com.idcard.model.BarcodeType;
import com.idcard.model.Profile;
import com.idcard.model.Template;
import com.idcard.service.BarcodeService;
import com.idcard.service.PDFService;
import com.idcard.service.QRCodeService;
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
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Implementation of PDFService using iText 8.
 * Generates a professional ID card PDF with:
 * - Profile photo
 * - Name, registration number, type
 * - QR code image
 * - Barcode image
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PDFServiceImpl implements PDFService {

    private final QRCodeService qrCodeService;
    private final BarcodeService barcodeService;

    // Brand color — dark navy
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(15, 52, 96);
    private static final DeviceRgb ACCENT_COLOR = new DeviceRgb(229, 57, 53);
    private static final DeviceRgb LIGHT_GRAY   = new DeviceRgb(245, 245, 245);

    @Override
    public byte[] generateIDCardPDF(Profile profile, Template template) {
        log.info("Generating PDF for profile: {}", profile.getId());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);

            // ID card size — credit card dimensions (85.6mm × 54mm) scaled up
            PageSize cardSize = new PageSize(340, 215);
            Document document = new Document(pdfDoc, cardSize);
            document.setMargins(0, 0, 0, 0);

            // ── Header bar ───────────────────────────────────────────────────
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBackgroundColor(HEADER_COLOR)
                    .setMarginBottom(0);

            Cell headerCell = new Cell()
                    .add(new Paragraph("ID CARD").setFontColor(ColorConstants.WHITE)
                            .setFontSize(14).setBold().setTextAlignment(TextAlignment.CENTER))
                    .add(new Paragraph(profile.getProfileType().name())
                            .setFontColor(ACCENT_COLOR).setFontSize(9)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                    .setPaddingTop(10).setPaddingBottom(8);
            headerTable.addCell(headerCell);
            document.add(headerTable);

            // ── Body: photo + info ────────────────────────────────────────────
            Table bodyTable = new Table(UnitValue.createPercentArray(new float[]{2, 3}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBackgroundColor(LIGHT_GRAY)
                    .setPaddingLeft(8).setPaddingRight(8).setPaddingTop(8);

            // Left column — Photo
            Cell photoCell = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                    .setPadding(6);
            if (profile.getPhotoPath() != null) {
                try {
                    byte[] photoBytes = Files.readAllBytes(Paths.get(profile.getPhotoPath()));
                    Image photo = new Image(ImageDataFactory.create(photoBytes))
                            .setWidth(72).setHeight(90)
                            .setBorder(new SolidBorder(ColorConstants.GRAY, 1));
                    photoCell.add(photo);
                } catch (IOException e) {
                    log.warn("Could not load photo for profile {}: {}", profile.getId(), e.getMessage());
                    photoCell.add(new Paragraph("No Photo")
                            .setFontSize(8).setItalic().setFontColor(ColorConstants.GRAY));
                }
            } else {
                photoCell.add(new Paragraph("No Photo")
                        .setFontSize(8).setItalic().setFontColor(ColorConstants.GRAY));
            }
            bodyTable.addCell(photoCell);

            // Right column — Info
            Cell infoCell = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                    .setPaddingLeft(8).setPaddingTop(6);

            infoCell.add(new Paragraph(profile.getFullName())
                    .setFontSize(11).setBold().setFontColor(HEADER_COLOR));
            infoCell.add(new Paragraph("Reg#: " + profile.getRegistrationNumber())
                    .setFontSize(8).setFontColor(ColorConstants.DARK_GRAY));

            if (profile.getEmail() != null)
                infoCell.add(new Paragraph("Email: " + profile.getEmail()).setFontSize(7.5f));
            if (profile.getPhone() != null)
                infoCell.add(new Paragraph("Phone: " + profile.getPhone()).setFontSize(7.5f));
            if (profile.getDateOfBirth() != null)
                infoCell.add(new Paragraph("DOB: " + profile.getDateOfBirth()).setFontSize(7.5f));

            bodyTable.addCell(infoCell);
            document.add(bodyTable);

            // ── QR Code + Barcode row ─────────────────────────────────────────
            Table codeTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBackgroundColor(LIGHT_GRAY)
                    .setPadding(6);

            // QR Code
            Cell qrCell = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER);
            try {
                byte[] qrBytes = qrCodeService.generateQRCodeBytes(
                        profile.getId(), profile.getRegistrationNumber());
                Image qrImage = new Image(ImageDataFactory.create(qrBytes))
                        .setWidth(55).setHeight(55)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER);
                qrCell.add(qrImage);
                qrCell.add(new Paragraph("Scan to Verify").setFontSize(6)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(ColorConstants.GRAY));
            } catch (Exception e) {
                log.warn("QR generation skipped: {}", e.getMessage());
            }
            codeTable.addCell(qrCell);

            // Barcode
            Cell barcodeCell = new Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER);
            try {
                BarcodeType barcodeType = (template != null && template.getBarcodeType() != null)
                        ? template.getBarcodeType() : BarcodeType.CODE_128;
                byte[] barcodeBytes = barcodeService.generateBarcodeBytes(
                        profile.getRegistrationNumber(), barcodeType);
                Image barcodeImage = new Image(ImageDataFactory.create(barcodeBytes))
                        .setWidth(100).setHeight(40)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER);
                barcodeCell.add(barcodeImage);
                barcodeCell.add(new Paragraph(profile.getRegistrationNumber()).setFontSize(6)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(ColorConstants.GRAY));
            } catch (Exception e) {
                log.warn("Barcode generation skipped: {}", e.getMessage());
            }
            codeTable.addCell(barcodeCell);
            document.add(codeTable);

            // ── Footer ────────────────────────────────────────────────────────
            Table footerTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBackgroundColor(HEADER_COLOR);

            Cell footerCell = new Cell()
                    .add(new Paragraph("This card is the property of the issuing organization.")
                            .setFontColor(ColorConstants.WHITE).setFontSize(6)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                    .setPaddingTop(4).setPaddingBottom(4);
            footerTable.addCell(footerCell);
            document.add(footerTable);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("PDF generation failed for profile {}: {}", profile.getId(), e.getMessage(), e);
            throw new RuntimeException("PDF generation failed", e);
        }
    }
}
