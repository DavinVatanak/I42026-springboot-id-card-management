package com.idcard.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for file storage operations.
 */
public interface FileStorageService {

    /**
     * Stores a photo file and returns its relative path.
     *
     * @param file the uploaded file (JPEG/PNG, max 5MB)
     * @return the relative storage path (e.g., "uploads/filename.jpg")
     */
    String storePhoto(MultipartFile file);

    /**
     * Deletes a stored file by its path.
     *
     * @param filePath the relative path to delete
     */
    void deleteFile(String filePath);
}
