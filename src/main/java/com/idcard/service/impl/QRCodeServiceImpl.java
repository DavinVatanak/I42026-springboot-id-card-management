package com.idcard.service.impl;

import com.idcard.service.QRCodeService;
import com.idcard.util.QRCodeGenerator;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Implementation of QRCodeService — delegates to QRCodeGenerator utility.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QRCodeServiceImpl implements QRCodeService {

    private final QRCodeGenerator qrCodeGenerator;

    @Override
    public String generateQRCodeBase64(Long profileId, String registrationNumber) {
        log.debug("Generating QR code for profile ID: {}", profileId);
        return qrCodeGenerator.generateQRCodeBase64(profileId, registrationNumber);
    }

    @Override
    public byte[] generateQRCodeBytes(Long profileId, String registrationNumber) {
        try {
            String content = "https://id-system.local/verify/" + profileId + "?reg=" + registrationNumber;
            return qrCodeGenerator.generateQRCodeBytes(content, 200, 200);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("QR code generation failed", e);
        }
    }
}
