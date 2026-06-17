package net.orderzone.idcard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.orderzone.idcard.dto.ProfileRequestDTO;
import net.orderzone.idcard.dto.ProfileResponseDTO;
import net.orderzone.idcard.exception.GlobalExceptionHandler;
import net.orderzone.idcard.exception.ResourceNotFoundException;
import net.orderzone.idcard.model.ProfileType;
import net.orderzone.idcard.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.mockito.ArgumentMatchers;

@WebMvcTest(ProfileController.class)
@Import({GlobalExceptionHandler.class, net.orderzone.idcard.config.SecurityConfig.class})
@DisplayName("ProfileController MockMvc Tests")
@WithMockUser
class ProfileControllerTest {

    @Autowired private MockMvc       mockMvc;
    @Autowired private ObjectMapper  objectMapper;
    @MockBean  private ProfileService profileService;

    private ProfileResponseDTO sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = ProfileResponseDTO.builder()
                .id(1L)
                .uuid("uuid-abc-123")
                .type(ProfileType.STUDENT)
                .fullName("John Doe")
                .email("john@example.com")
                .phone("+855-12-000-001")
                .registrationNumber("2026-STU-001")
                .build();
    }

    @Test
    @DisplayName("POST /api/profiles → 201 Created")
    void createProfile_ReturnsCreated() throws Exception {
        ProfileRequestDTO request = ProfileRequestDTO.builder()
                .type(ProfileType.STUDENT)
                .fullName("John Doe")
                .email("john@example.com")
                .phone("+855-12-000-001")
                .build();

        when(profileService.createProfile(ArgumentMatchers.any(ProfileRequestDTO.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.registrationNumber").value("2026-STU-001"))
                .andExpect(jsonPath("$.uuid").value("uuid-abc-123"));
    }

    @Test
    @DisplayName("POST /api/profiles → 400 when fullName blank")
    void createProfile_Validation_Returns400() throws Exception {
        ProfileRequestDTO invalid = ProfileRequestDTO.builder()
                .type(ProfileType.STUDENT)
                .fullName("")         // blank — should fail
                .email("not-email")  // invalid email
                .phone("")           // blank
                .build();

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    @Test
    @DisplayName("GET /api/profiles → 200 with list")
    void getAllProfiles_Returns200() throws Exception {
        when(profileService.getAllProfiles()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("STUDENT"));
    }

    @Test
    @DisplayName("GET /api/profiles/1 → 200 OK")
    void getProfileById_Found() throws Exception {
        when(profileService.getProfileById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/profiles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/profiles/999 → 404")
    void getProfileById_NotFound() throws Exception {
        when(profileService.getProfileById(999L))
                .thenThrow(new ResourceNotFoundException("Profile", "id", 999L));

        mockMvc.perform(get("/api/profiles/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("999")));
    }

    @Test
    @DisplayName("DELETE /api/profiles/1 → 200 OK")
    void deleteProfile_Returns200() throws Exception {
        doNothing().when(profileService).deleteProfile(1L);

        mockMvc.perform(delete("/api/profiles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("deleted")));
    }
}
