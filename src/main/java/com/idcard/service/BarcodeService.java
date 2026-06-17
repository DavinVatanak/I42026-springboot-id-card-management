package com.idcard.service;

import com.idcard.model.BarcodeType;

/**
 * Service interface for barcode image generation.
 */
public interface BarcodeService {

    /**
     * Generates a barcode as a Base64-encoded PNG.
     *
     * @param data        the text/number to encode
     * @param barcodeType the barcode format
     * @return Base64 PNG data URI
     */
    String generateBarcodeBase64(String data, BarcodeType barcodeType);

    /**
     * Generates a barcode as raw PNG bytes.
     *
     * @param data        the text/number to encode
     * @param barcodeType the barcode format
     * @return PNG bytes
     */
    byte[] generateBarcodeBytes(String data, BarcodeType barcodeType);
}
