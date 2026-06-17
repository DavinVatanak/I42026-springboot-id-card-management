package net.orderzone.idcard.controller;

import net.orderzone.idcard.dto.ProfileRequestDTO;
import net.orderzone.idcard.dto.ProfileResponseDTO;
import net.orderzone.idcard.model.ProfileType;
import net.orderzone.idcard.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Profile CRUD and photo upload.
 * Base path: /api/profiles
 */
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Profile", description = "ID Card Profile Management")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    @Operation(summary = "Create a new profile")
    public ResponseEntity<ProfileResponseDTO> createProfile(
            @Valid @RequestBody ProfileRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profileService.createProfile(request));
    }

    @GetMapping
    @Operation(summary = "List profiles (with optional name search or type filter)")
    public ResponseEntity<List<ProfileResponseDTO>> getAllProfiles(
            @Parameter(description = "Search by name (partial, case-insensitive)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filter by profile type: STUDENT, EMPLOYEE, USER")
            @RequestParam(required = false) ProfileType type) {

        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(profileService.searchProfilesByName(name));
        }
        if (type != null) {
            return ResponseEntity.ok(profileService.getProfilesByType(type));
        }
        return ResponseEntity.ok(profileService.getAllProfiles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a profile by database ID")
    public ResponseEntity<ProfileResponseDTO> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfileById(id));
    }

    @GetMapping("/uuid/{uuid}")
    @Operation(summary = "Get a profile by its stable UUID (used in QR verification)")
    public ResponseEntity<ProfileResponseDTO> getProfileByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(profileService.getProfileByUuid(uuid));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing profile")
    public ResponseEntity<ProfileResponseDTO> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody ProfileRequestDTO request) {
        return ResponseEntity.ok(profileService.updateProfile(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a profile")
    public ResponseEntity<Map<String, String>> deleteProfile(@PathVariable Long id) {
        profileService.deleteProfile(id);
        return ResponseEntity.ok(Map.of("message", "Profile " + id + " deleted successfully"));
    }

    @PostMapping(value = "/upload-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a profile photo (JPEG/PNG, max 5 MB)")
    public ResponseEntity<ProfileResponseDTO> uploadPhoto(
            @RequestParam Long profileId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(profileService.uploadPhoto(profileId, file));
    }
}
