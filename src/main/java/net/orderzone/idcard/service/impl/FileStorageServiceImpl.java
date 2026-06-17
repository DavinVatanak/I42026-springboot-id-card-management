package net.orderzone.idcard.service.impl;

import net.orderzone.idcard.exception.InvalidFileException;
import net.orderzone.idcard.service.FileStorageService;
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
 * Stores uploaded photos in the configured upload directory.
 * Returns only the filename (matching Profile.photoFileName).
 */
@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private static final List<String> ALLOWED_CONTENT_TYPES =
            Arrays.asList("image/jpeg", "image/jpg", "image/png");
    private static final List<String> ALLOWED_EXTENSIONS =
            Arrays.asList("jpg", "jpeg", "png");
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024; // 5 MB

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public String storePhoto(MultipartFile file) {
        validateFile(file);

        String original  = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "photo");
        String extension = getExtension(original);
        String fileName  = UUID.randomUUID() + "." + extension;

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);
            log.info("Photo stored: {}", fileName);
            return fileName; // ← only the filename, not full path
        } catch (IOException e) {
            throw new RuntimeException("Could not store photo", e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isBlank()) return;
        try {
            Path path = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) log.info("Deleted photo: {}", fileName);
        } catch (IOException e) {
            log.warn("Could not delete file {}: {}", fileName, e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new InvalidFileException("File must not be empty");
        if (file.getSize() > MAX_FILE_SIZE)
            throw new InvalidFileException(String.format(
                    "File size %.2f MB exceeds the 5 MB limit",
                    file.getSize() / (1024.0 * 1024.0)));

        String ct = file.getContentType();
        if (ct == null || !ALLOWED_CONTENT_TYPES.contains(ct.toLowerCase()))
            throw new InvalidFileException("Only JPEG and PNG images are accepted");

        String ext = getExtension(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
        if (!ALLOWED_EXTENSIONS.contains(ext.toLowerCase()))
            throw new InvalidFileException("Invalid extension. Allowed: jpg, jpeg, png");
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1)
            throw new InvalidFileException("File must have a valid extension");
        return filename.substring(dot + 1).toLowerCase();
    }
}
