package net.orderzone.idcard.service.impl;

import net.orderzone.idcard.dto.ProfileRequestDTO;
import net.orderzone.idcard.dto.ProfileResponseDTO;
import net.orderzone.idcard.exception.DuplicateResourceException;
import net.orderzone.idcard.exception.ResourceNotFoundException;
import net.orderzone.idcard.mapper.ProfileMapper;
import net.orderzone.idcard.model.Profile;
import net.orderzone.idcard.model.ProfileType;
import net.orderzone.idcard.model.Template;
import net.orderzone.idcard.repository.ProfileRepository;
import net.orderzone.idcard.repository.TemplateRepository;
import net.orderzone.idcard.service.FileStorageService;
import net.orderzone.idcard.service.ProfileService;
import net.orderzone.idcard.util.RegistrationNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ProfileService implementation.
 *
 * Key behaviours:
 * - uuid is generated here (not in entity lifecycle) so it is available immediately after build.
 * - registrationNumber is generated after email uniqueness check.
 * - template is resolved from templateId before mapping.
 * - photoFileName / photoContentType are stored as separate columns (matching the entity).
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository       profileRepository;
    private final TemplateRepository      templateRepository;
    private final ProfileMapper           profileMapper;
    private final RegistrationNumberGenerator regNumberGenerator;
    private final FileStorageService      fileStorageService;

    // ─── Create ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileResponseDTO createProfile(ProfileRequestDTO request) {
        log.info("Creating profile: type={}, email={}", request.getType(), request.getEmail());

        if (request.getEmail() != null && profileRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Profile", "email", request.getEmail());
        }

        // Map basic fields
        Profile profile = profileMapper.toEntity(request);

        // Set UUID
        profile.setUuid(UUID.randomUUID().toString());

        // Resolve template FK
        if (request.getTemplateId() != null) {
            Template template = templateRepository.findById(request.getTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException("Template", "id", request.getTemplateId()));
            profile.setTemplate(template);
        }

        // Generate registration number
        profile.setRegistrationNumber(regNumberGenerator.generate(request.getType()));

        Profile saved = profileRepository.save(profile);
        log.info("Profile created: id={}, reg={}", saved.getId(), saved.getRegistrationNumber());
        return profileMapper.toResponseDTO(saved);
    }

    // ─── Read ─────────────────────────────────────────────────────────────────

    @Override
    public ProfileResponseDTO getProfileById(Long id) {
        return profileMapper.toResponseDTO(findOrThrow(id));
    }

    @Override
    public ProfileResponseDTO getProfileByUuid(String uuid) {
        Profile profile = profileRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "uuid", uuid));
        return profileMapper.toResponseDTO(profile);
    }

    @Override
    public List<ProfileResponseDTO> getAllProfiles() {
        return profileRepository.findAll().stream()
                .map(profileMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfileResponseDTO> searchProfilesByName(String name) {
        return profileRepository.searchByFullName(name).stream()
                .map(profileMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfileResponseDTO> getProfilesByType(ProfileType type) {
        return profileRepository.findByType(type).stream()
                .map(profileMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileResponseDTO updateProfile(Long id, ProfileRequestDTO request) {
        Profile profile = findOrThrow(id);

        // Email uniqueness check (only if email actually changed)
        if (request.getEmail() != null
                && !request.getEmail().equalsIgnoreCase(profile.getEmail())
                && profileRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Profile", "email", request.getEmail());
        }

        profileMapper.updateEntityFromDTO(request, profile);

        // Re-resolve template if templateId changed
        if (request.getTemplateId() != null) {
            Template template = templateRepository.findById(request.getTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException("Template", "id", request.getTemplateId()));
            profile.setTemplate(template);
        }

        return profileMapper.toResponseDTO(profileRepository.save(profile));
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteProfile(Long id) {
        Profile profile = findOrThrow(id);
        if (profile.hasPhoto()) {
            fileStorageService.deleteFile(profile.getPhotoFileName());
        }
        profileRepository.delete(profile);
    }

    // ─── Photo Upload ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ProfileResponseDTO uploadPhoto(Long profileId, MultipartFile file) {
        Profile profile = findOrThrow(profileId);

        // Delete old photo if present
        if (profile.hasPhoto()) {
            fileStorageService.deleteFile(profile.getPhotoFileName());
        }

        // Store and update entity fields
        String storedFileName = fileStorageService.storePhoto(file);
        profile.setPhotoFileName(storedFileName);
        profile.setPhotoContentType(file.getContentType());

        return profileMapper.toResponseDTO(profileRepository.save(profile));
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private Profile findOrThrow(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", id));
    }
}
