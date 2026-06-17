package net.orderzone.idcard.util;

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
 * Generates QR codes using ZXing.
 * Encodes: profile UUID + registration number + verification URL.
 */
@Component
@Slf4j
public class QRCodeGenerator {

    @Value("${app.verification.base-url:https://id-system.local/verify}")
    private String verificationBaseUrl;

    private static final int QR_WIDTH  = 200;
    private static final int QR_HEIGHT = 200;

    /**
     * Generates a QR code as a Base64-encoded PNG data URI.
     *
     * @param uuid               the profile's stable UUID
     * @param registrationNumber the human-readable registration number
     * @return "data:image/png;base64,..." string for inline HTML embedding
     */
    public String generateQRCodeBase64(String uuid, String registrationNumber) {
        String content = buildQRContent(uuid, registrationNumber);
        try {
            byte[] png = generateQRCodeBytes(content, QR_WIDTH, QR_HEIGHT);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(png);
        } catch (WriterException | IOException e) {
            log.error("QR code generation failed for uuid {}: {}", uuid, e.getMessage());
            throw new RuntimeException("QR code generation failed", e);
        }
    }

    /**
     * Generates a QR code as raw PNG bytes.
     *
     * @param content the text/URL to encode
     * @param width   image width in pixels
     * @param height  image height in pixels
     * @return PNG byte array
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

    private String buildQRContent(String uuid, String registrationNumber) {
        return String.format("%s/%s?reg=%s", verificationBaseUrl, uuid, registrationNumber);
    }
}
