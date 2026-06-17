package com.idcard.service;

/**
 * Service interface for QR code generation.
 */
public interface QRCodeService {

    /**
     * Generates a QR code as a Base64-encoded PNG.
     *
     * @param profileId          the profile ID
     * @param registrationNumber the registration number
     * @return Base64 PNG data URI
     */
    String generateQRCodeBase64(Long profileId, String registrationNumber);

    /**
     * Generates a QR code as raw PNG bytes.
     *
     * @param profileId          the profile ID
     * @param registrationNumber the registration number
     * @return PNG bytes
     */
    byte[] generateQRCodeBytes(Long profileId, String registrationNumber);
}
