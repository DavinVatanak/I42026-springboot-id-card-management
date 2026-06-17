package net.orderzone.idcard.service.impl;

import net.orderzone.idcard.model.BarcodeType;
import net.orderzone.idcard.service.BarcodeService;
import net.orderzone.idcard.util.BarcodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BarcodeServiceImpl implements BarcodeService {

    private final BarcodeGenerator barcodeGenerator;

    @Override
    public String generateBarcodeBase64(String data, BarcodeType barcodeType) {
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
