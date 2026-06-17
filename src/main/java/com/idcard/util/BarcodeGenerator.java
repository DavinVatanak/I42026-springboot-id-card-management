package com.idcard.util;

import com.idcard.model.BarcodeType;
import lombok.extern.slf4j.Slf4j;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;

/**
 * Utility for generating barcode images using Barcode4J.
 * Supports CODE_128 and EAN_13 formats.
 */
@Component
@Slf4j
public class BarcodeGenerator {

    private static final int DPI = 150;
    private static final double BAR_WIDTH = 0.8; // mm

    /**
     * Generates a barcode image as a Base64-encoded PNG string.
     *
     * @param data        the text/number to encode into the barcode
     * @param barcodeType the barcode format (CODE_128 or EAN_13)
     * @return Base64-encoded PNG string (data:image/png;base64,...)
     */
    public String generateBarcodeBase64(String data, BarcodeType barcodeType) {
        try {
            byte[] pngBytes = generateBarcodeBytes(data, barcodeType);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(pngBytes);
        } catch (IOException e) {
            log.error("Failed to generate barcode for '{}': {}", data, e.getMessage());
            throw new RuntimeException("Barcode generation failed", e);
        }
    }

    /**
     * Generates a barcode as raw PNG bytes.
     *
     * @param data        the value to encode
     * @param barcodeType the barcode format
     * @return PNG bytes
     */
    public byte[] generateBarcodeBytes(String data, BarcodeType barcodeType) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                    baos, "image/x-png", DPI,
                    BufferedImage.TYPE_BYTE_BINARY, false, 0);

            switch (barcodeType) {
                case CODE_128 -> {
                    Code128Bean bean = new Code128Bean();
                    bean.setModuleWidth(BAR_WIDTH);
                    bean.setBarHeight(15); // mm
                    bean.doQuietZone(true);
                    bean.generateBarcode(canvas, data);
                }
                case EAN_13 -> {
                    // EAN-13 requires exactly 13 digits
                    String ean13Data = padOrTrimForEAN13(data);
                    EAN13Bean bean = new EAN13Bean();
                    bean.setModuleWidth(BAR_WIDTH);
                    bean.doQuietZone(true);
                    bean.generateBarcode(canvas, ean13Data);
                }
            }

            canvas.finish();
            return baos.toByteArray();
        }
    }

    /**
     * Ensures data is exactly 12 digits for EAN-13 (the 13th digit is check digit).
     * Pads with leading zeros or trims to 12 digits.
     */
    private String padOrTrimForEAN13(String data) {
        // Remove non-digit characters
        String digits = data.replaceAll("\\D", "");
        if (digits.length() >= 12) {
            return digits.substring(0, 12);
        }
        // Pad with leading zeros to reach 12 digits
        return String.format("%012d", Long.parseLong(digits.isEmpty() ? "0" : digits));
    }
}
