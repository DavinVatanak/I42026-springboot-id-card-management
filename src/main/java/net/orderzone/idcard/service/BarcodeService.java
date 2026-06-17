package net.orderzone.idcard.service;

import net.orderzone.idcard.model.BarcodeType;

public interface BarcodeService {

    String generateBarcodeBase64(String data, BarcodeType barcodeType);

    byte[] generateBarcodeBytes(String data, BarcodeType barcodeType);
}
