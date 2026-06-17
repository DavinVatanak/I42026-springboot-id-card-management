package net.orderzone.idcard.service.impl;

import net.orderzone.idcard.service.QRCodeService;
import net.orderzone.idcard.util.QRCodeGenerator;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class QRCodeServiceImpl implements QRCodeService {

    private final QRCodeGenerator qrCodeGenerator;

    @Value("${app.verification.base-url:https://id-system.local/verify}")
    private String baseUrl;

    @Override
    public String generateQRCodeBase64(String uuid, String registrationNumber) {
        return qrCodeGenerator.generateQRCodeBase64(uuid, registrationNumber);
    }

    @Override
    public byte[] generateQRCodeBytes(String uuid, String registrationNumber) {
        String content = String.format("%s/%s?reg=%s", baseUrl, uuid, registrationNumber);
        try {
            return qrCodeGenerator.generateQRCodeBytes(content, 200, 200);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("QR code generation failed", e);
        }
    }
}
