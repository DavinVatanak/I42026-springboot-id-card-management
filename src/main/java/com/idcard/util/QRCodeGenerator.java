package com.idcard.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility for generating QR codes using the ZXing library.
 *
 * QR code content encodes:
 *   - Profile ID
 *   - Registration number
 *   - Verification URL
 *
 * Usage: call generateQRCodeBase64() to get an inline-embeddable PNG string.
 */
@Component
@Slf4j
public class QRCodeGenerator {

    @Value("${app.verification.base-url:https://id-system.local/verify}")
    private String verificationBaseUrl;

    private static final int QR_WIDTH = 200;
    private static final int QR_HEIGHT = 200;

    /**
     * Generates a QR code image as a Base64-encoded PNG string.
     *
     * @param profileId          the profile's database ID
     * @param registrationNumber the profile's registration number
     * @return Base64-encoded PNG image string (data:image/png;base64,...)
     */
    public String generateQRCodeBase64(Long profileId, String registrationNumber) {
        String content = buildQRContent(profileId, registrationNumber);
        try {
            byte[] pngBytes = generateQRCodeBytes(content, QR_WIDTH, QR_HEIGHT);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngBytes);
        } catch (WriterException | IOException e) {
            log.error("Failed to generate QR code for profile {}: {}", profileId, e.getMessage());
            throw new RuntimeException("QR code generation failed", e);
        }
    }

    /**
     * Generates a QR code as raw PNG bytes.
     *
     * @param content the text content to encode
     * @param width   image width in pixels
     * @param height  image height in pixels
     * @return PNG bytes
     */
    public byte[] generateQRCodeBytes(String content, int width, int height)
            throws WriterException, IOException {

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            return baos.toByteArray();
        }
    }

    /**
     * Builds the URL-encoded content embedded in the QR code.
     */
    private String buildQRContent(Long profileId, String registrationNumber) {
        return String.format("%s/%d?reg=%s", verificationBaseUrl, profileId, registrationNumber);
    }
}
