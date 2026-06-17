package com.idcard.service.impl;

import com.idcard.exception.InvalidFileException;
import com.idcard.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of FileStorageService.
 * Stores uploaded photos in a local directory (default: uploads/).
 * Enforces JPEG/PNG type and 5MB size limits.
 */
@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    /** Allowed MIME types for photo uploads. */
    private static final List<String> ALLOWED_CONTENT_TYPES =
            Arrays.asList("image/jpeg", "image/jpg", "image/png");

    /** Allowed file extensions. */
    private static final List<String> ALLOWED_EXTENSIONS =
            Arrays.asList("jpg", "jpeg", "png");

    /** Maximum file size: 5 MB. */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public String storePhoto(MultipartFile file) {
        validateFile(file);

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "photo");
        String extension = getExtension(originalFilename);

        // Generate a unique filename to avoid conflicts
        String uniqueFilename = UUID.randomUUID() + "." + extension;

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            Path targetPath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String storedPath = uploadDir + "/" + uniqueFilename;
            log.info("Photo stored at: {}", storedPath);
            return storedPath;

        } catch (IOException e) {
            log.error("Failed to store file: {}", e.getMessage());
            throw new RuntimeException("Could not store photo. Please try again.", e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;

        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) {
                log.info("Deleted file: {}", filePath);
            }
        } catch (IOException e) {
            log.warn("Could not delete file {}: {}", filePath, e.getMessage());
        }
    }

    // ─── Validation helpers ───────────────────────────────────────────────────

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File must not be empty");
        }

        // Size validation
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException(
                    String.format("File size %.2f MB exceeds the maximum allowed 5 MB",
                            file.getSize() / (1024.0 * 1024.0)));
        }

        // MIME type validation
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidFileException(
                    "Invalid file type. Only JPEG and PNG images are accepted.");
        }

        // Extension validation
        String extension = getExtension(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new InvalidFileException(
                    "Invalid file extension. Allowed: jpg, jpeg, png");
        }
    }

    /**
     * Extracts the lowercase file extension from a filename.
     */
    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            throw new InvalidFileException("File must have a valid extension (jpg, jpeg, png)");
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }
}
