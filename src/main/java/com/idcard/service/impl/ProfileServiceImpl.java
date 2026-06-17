package com.idcard.service.impl;

import com.idcard.dto.ProfileRequestDTO;
import com.idcard.dto.ProfileResponseDTO;
import com.idcard.exception.DuplicateResourceException;
import com.idcard.exception.ResourceNotFoundException;
import com.idcard.mapper.ProfileMapper;
import com.idcard.model.Profile;
import com.idcard.model.ProfileType;
import com.idcard.repository.ProfileRepository;
import com.idcard.service.FileStorageService;
import com.idcard.service.ProfileService;
import com.idcard.util.RegistrationNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ProfileService.
 * Handles all business logic for profile CRUD and photo upload.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final RegistrationNumberGenerator registrationNumberGenerator;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public ProfileResponseDTO createProfile(ProfileRequestDTO request) {
        log.info("Creating new profile for email: {}", request.getEmail());

        // Validate uniqueness
        if (profileRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Profile", "email", request.getEmail());
        }

        // Map DTO → entity
        Profile profile = profileMapper.toEntity(request);

        // Generate unique registration number BEFORE saving
        String regNumber = registrationNumberGenerator.generate(request.getProfileType());
        profile.setRegistrationNumber(regNumber);

        Profile saved = profileRepository.save(profile);
        log.info("Profile created with ID: {} and reg#: {}", saved.getId(), saved.getRegistrationNumber());

        return profileMapper.toResponseDTO(saved);
    }

    @Override
    public ProfileResponseDTO getProfileById(Long id) {
        Profile profile = findOrThrow(id);
        return profileMapper.toResponseDTO(profile);
    }

    @Override
    public List<ProfileResponseDTO> getAllProfiles() {
        return profileRepository.findAll()
                .stream()
                .map(profileMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfileResponseDTO> searchProfilesByName(String name) {
        return profileRepository.searchByName(name)
                .stream()
                .map(profileMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfileResponseDTO> getProfilesByType(ProfileType type) {
        return profileRepository.findByProfileType(type)
                .stream()
                .map(profileMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProfileResponseDTO updateProfile(Long id, ProfileRequestDTO request) {
        log.info("Updating profile ID: {}", id);

        Profile profile = findOrThrow(id);

        // Check email uniqueness only if it changed
        if (!profile.getEmail().equalsIgnoreCase(request.getEmail())
                && profileRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Profile", "email", request.getEmail());
        }

        profileMapper.updateEntityFromDTO(request, profile);
        Profile updated = profileRepository.save(profile);

        return profileMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteProfile(Long id) {
        log.info("Deleting profile ID: {}", id);
        Profile profile = findOrThrow(id);

        // Delete associated photo if present
        if (profile.getPhotoPath() != null) {
            fileStorageService.deleteFile(profile.getPhotoPath());
        }

        profileRepository.delete(profile);
    }

    @Override
    @Transactional
    public ProfileResponseDTO uploadPhoto(Long profileId, MultipartFile file) {
        log.info("Uploading photo for profile ID: {}", profileId);
        Profile profile = findOrThrow(profileId);

        // Delete old photo if present
        if (profile.getPhotoPath() != null) {
            fileStorageService.deleteFile(profile.getPhotoPath());
        }

        String storedPath = fileStorageService.storePhoto(file);
        profile.setPhotoPath(storedPath);
        Profile saved = profileRepository.save(profile);

        return profileMapper.toResponseDTO(saved);
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private Profile findOrThrow(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", id));
    }
}
