package com.idcard.service;

import com.idcard.dto.ProfileRequestDTO;
import com.idcard.dto.ProfileResponseDTO;
import com.idcard.model.ProfileType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface defining all profile management operations.
 */
public interface ProfileService {

    ProfileResponseDTO createProfile(ProfileRequestDTO request);

    ProfileResponseDTO getProfileById(Long id);

    List<ProfileResponseDTO> getAllProfiles();

    List<ProfileResponseDTO> searchProfilesByName(String name);

    List<ProfileResponseDTO> getProfilesByType(ProfileType type);

    ProfileResponseDTO updateProfile(Long id, ProfileRequestDTO request);

    void deleteProfile(Long id);

    ProfileResponseDTO uploadPhoto(Long profileId, MultipartFile file);
}
