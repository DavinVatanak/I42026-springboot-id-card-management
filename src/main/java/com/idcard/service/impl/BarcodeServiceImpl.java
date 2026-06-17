package com.idcard.service.impl;

import com.idcard.model.BarcodeType;
import com.idcard.service.BarcodeService;
import com.idcard.util.BarcodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of BarcodeService — delegates to BarcodeGenerator utility.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BarcodeServiceImpl implements BarcodeService {

    private final BarcodeGenerator barcodeGenerator;

    @Override
    public String generateBarcodeBase64(String data, BarcodeType barcodeType) {
        log.debug("Generating {} barcode for: {}", barcodeType, data);
        return barcodeGenerator.generateBarcodeBase64(data, barcodeType);
    }

    @Override
    public byte[] generateBarcodeBytes(String data, BarcodeType barcodeType) {
        try {
            return barcodeGenerator.generateBarcodeBytes(data, barcodeType);
        } catch (Exception e) {
            throw new RuntimeException("Barcode generation failed", e);
        }
    }
}
