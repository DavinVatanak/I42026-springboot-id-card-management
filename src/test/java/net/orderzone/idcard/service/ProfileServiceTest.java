package net.orderzone.idcard.service;

import net.orderzone.idcard.dto.ProfileRequestDTO;
import net.orderzone.idcard.dto.ProfileResponseDTO;
import net.orderzone.idcard.exception.DuplicateResourceException;
import net.orderzone.idcard.exception.ResourceNotFoundException;
import net.orderzone.idcard.mapper.ProfileMapper;
import net.orderzone.idcard.model.Profile;
import net.orderzone.idcard.model.ProfileType;
import net.orderzone.idcard.repository.ProfileRepository;
import net.orderzone.idcard.repository.TemplateRepository;
import net.orderzone.idcard.service.impl.ProfileServiceImpl;
import net.orderzone.idcard.util.RegistrationNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProfileServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileService Unit Tests")
class ProfileServiceTest {

    @Mock private ProfileRepository         profileRepository;
    @Mock private TemplateRepository        templateRepository;
    @Mock private ProfileMapper             profileMapper;
    @Mock private RegistrationNumberGenerator regNumberGenerator;
    @Mock private FileStorageService        fileStorageService;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private ProfileRequestDTO  validRequest;
    private Profile            savedProfile;
    private ProfileResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        validRequest = ProfileRequestDTO.builder()
                .type(ProfileType.STUDENT)
                .fullName("John Doe")
                .email("john@example.com")
                .phone("+855-12-000-001")
                .build();

        savedProfile = Profile.builder()
                .id(1L)
                .uuid("test-uuid-1234")
                .type(ProfileType.STUDENT)
                .fullName("John Doe")
                .email("john@example.com")
                .phone("+855-12-000-001")
                .registrationNumber("2026-STU-001")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        responseDTO = ProfileResponseDTO.builder()
                .id(1L)
                .uuid("test-uuid-1234")
                .type(ProfileType.STUDENT)
                .fullName("John Doe")
                .email("john@example.com")
                .registrationNumber("2026-STU-001")
                .build();
    }

    @Test
    @DisplayName("createProfile: should create and return profile with reg number")
    void createProfile_Success() {
        when(profileRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(profileMapper.toEntity(validRequest)).thenReturn(savedProfile);
        when(regNumberGenerator.generate(ProfileType.STUDENT)).thenReturn("2026-STU-001");
        when(profileRepository.save(any(Profile.class))).thenReturn(savedProfile);
        when(profileMapper.toResponseDTO(savedProfile)).thenReturn(responseDTO);

        ProfileResponseDTO result = profileService.createProfile(validRequest);

        assertThat(result).isNotNull();
        assertThat(result.getRegistrationNumber()).isEqualTo("2026-STU-001");
        verify(profileRepository).save(any(Profile.class));
        verify(regNumberGenerator).generate(ProfileType.STUDENT);
    }

    @Test
    @DisplayName("createProfile: should throw DuplicateResourceException on duplicate email")
    void createProfile_DuplicateEmail_Throws() {
        when(profileRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> profileService.createProfile(validRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("john@example.com");

        verify(profileRepository, never()).save(any());
    }

    @Test
    @DisplayName("getProfileById: should return profile when found")
    void getProfileById_Found() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(savedProfile));
        when(profileMapper.toResponseDTO(savedProfile)).thenReturn(responseDTO);

        ProfileResponseDTO result = profileService.getProfileById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUuid()).isEqualTo("test-uuid-1234");
    }

    @Test
    @DisplayName("getProfileById: should throw ResourceNotFoundException when not found")
    void getProfileById_NotFound_Throws() {
        when(profileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.getProfileById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("getAllProfiles: should return full list")
    void getAllProfiles_ReturnsList() {
        when(profileRepository.findAll()).thenReturn(List.of(savedProfile));
        when(profileMapper.toResponseDTO(savedProfile)).thenReturn(responseDTO);

        List<ProfileResponseDTO> results = profileService.getAllProfiles();

        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("deleteProfile: should call deleteFile when profile has a photo")
    void deleteProfile_WithPhoto_DeletesFile() {
        savedProfile.setPhotoFileName("abc-123.jpg");
        when(profileRepository.findById(1L)).thenReturn(Optional.of(savedProfile));

        profileService.deleteProfile(1L);

        verify(fileStorageService).deleteFile("abc-123.jpg");
        verify(profileRepository).delete(savedProfile);
    }

    @Test
    @DisplayName("deleteProfile: should throw when profile not found")
    void deleteProfile_NotFound_Throws() {
        when(profileRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.deleteProfile(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
