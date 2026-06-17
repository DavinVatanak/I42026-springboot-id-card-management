package net.orderzone.idcard.service;

import net.orderzone.idcard.dto.ProfileRequestDTO;
import net.orderzone.idcard.dto.ProfileResponseDTO;
import net.orderzone.idcard.model.ProfileType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProfileService {

    ProfileResponseDTO createProfile(ProfileRequestDTO request);

    ProfileResponseDTO getProfileById(Long id);

    ProfileResponseDTO getProfileByUuid(String uuid);

    List<ProfileResponseDTO> getAllProfiles();

    List<ProfileResponseDTO> searchProfilesByName(String name);

    List<ProfileResponseDTO> getProfilesByType(ProfileType type);

    ProfileResponseDTO updateProfile(Long id, ProfileRequestDTO request);

    void deleteProfile(Long id);

    ProfileResponseDTO uploadPhoto(Long profileId, MultipartFile file);
}
