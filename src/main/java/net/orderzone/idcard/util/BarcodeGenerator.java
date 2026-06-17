package net.orderzone.idcard.util;

import net.orderzone.idcard.model.BarcodeType;
import lombok.extern.slf4j.Slf4j;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Generates barcode images using Barcode4J.
 * Supports CODE_128 and EAN_13.
 */
@Component
@Slf4j
public class BarcodeGenerator {

    private static final int    DPI       = 150;
    private static final double BAR_WIDTH = 0.8; // mm

    /**
     * Returns a Base64-encoded PNG data URI of the barcode.
     */
    public String generateBarcodeBase64(String data, BarcodeType barcodeType) {
        try {
            byte[] png = generateBarcodeBytes(data, barcodeType);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(png);
        } catch (IOException e) {
            log.error("Barcode generation failed for '{}': {}", data, e.getMessage());
            throw new RuntimeException("Barcode generation failed", e);
        }
    }

    /**
     * Returns raw PNG bytes of the barcode.
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
                    bean.setBarHeight(15);
                    bean.doQuietZone(true);
                    bean.generateBarcode(canvas, data);
                }
                case EAN_13 -> {
                    EAN13Bean bean = new EAN13Bean();
                    bean.setModuleWidth(BAR_WIDTH);
                    bean.doQuietZone(true);
                    bean.generateBarcode(canvas, padOrTrimForEAN13(data));
                }
            }

            canvas.finish();
            return baos.toByteArray();
        }
    }

    /** EAN-13 needs exactly 12 numeric digits (13th is check digit). */
    private String padOrTrimForEAN13(String data) {
        String digits = data.replaceAll("\\D", "");
        if (digits.length() >= 12) return digits.substring(0, 12);
        return String.format("%012d", Long.parseLong(digits.isEmpty() ? "0" : digits));
    }
}
