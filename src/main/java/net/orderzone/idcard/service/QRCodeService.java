package net.orderzone.idcard.service;

public interface QRCodeService {

    /** Returns a Base64 PNG data URI for inline HTML embedding. */
    String generateQRCodeBase64(String uuid, String registrationNumber);

    /** Returns raw PNG bytes for PDF embedding. */
    byte[] generateQRCodeBytes(String uuid, String registrationNumber);
}
