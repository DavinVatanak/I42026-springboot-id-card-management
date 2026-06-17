package com.idcard.controller;

import com.idcard.dto.ProfileRequestDTO;
import com.idcard.dto.ProfileResponseDTO;
import com.idcard.model.ProfileType;
import com.idcard.service.ProfileService;
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
 * REST Controller for Profile CRUD and photo upload operations.
 * Base path: /api/profiles
 */
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Profile", description = "ID Card Profile Management APIs")
public class ProfileController {

    private final ProfileService profileService;

    // ─── CREATE ───────────────────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Create a new profile", description = "Creates a new ID card profile with auto-generated registration number")
    public ResponseEntity<ProfileResponseDTO> createProfile(
            @Valid @RequestBody ProfileRequestDTO request) {
        log.info("POST /api/profiles");
        ProfileResponseDTO created = profileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ─── READ ─────────────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Get all profiles", description = "Returns all profiles. Supports optional name search and type filter.")
    public ResponseEntity<List<ProfileResponseDTO>> getAllProfiles(
            @Parameter(description = "Search profiles by name (partial match)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filter by profile type")
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
    @Operation(summary = "Get a profile by ID")
    public ResponseEntity<ProfileResponseDTO> getProfileById(
            @Parameter(description = "Profile ID") @PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfileById(id));
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing profile")
    public ResponseEntity<ProfileResponseDTO> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody ProfileRequestDTO request) {
        return ResponseEntity.ok(profileService.updateProfile(id, request));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a profile by ID")
    public ResponseEntity<Map<String, String>> deleteProfile(@PathVariable Long id) {
        profileService.deleteProfile(id);
        return ResponseEntity.ok(Map.of(
                "message", "Profile with ID " + id + " deleted successfully"
        ));
    }

    // ─── PHOTO UPLOAD ─────────────────────────────────────────────────────────

    @PostMapping(value = "/upload-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a photo for a profile",
               description = "Accepts JPEG/PNG up to 5MB. Replaces any existing photo.")
    public ResponseEntity<ProfileResponseDTO> uploadPhoto(
            @RequestParam Long profileId,
            @RequestParam("file") MultipartFile file) {
        log.info("POST /api/profiles/upload-photo for profile: {}", profileId);
        return ResponseEntity.ok(profileService.uploadPhoto(profileId, file));
    }
}
