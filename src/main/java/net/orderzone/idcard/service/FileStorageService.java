package net.orderzone.idcard.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Validates and stores a photo. Returns the stored filename (not the full path).
     */
    String storePhoto(MultipartFile file);

    void deleteFile(String fileName);
}
