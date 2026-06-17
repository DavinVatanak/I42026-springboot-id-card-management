package net.orderzone.idcard.service;

import net.orderzone.idcard.dto.TemplateRequestDTO;
import net.orderzone.idcard.dto.TemplateResponseDTO;
import net.orderzone.idcard.exception.DuplicateResourceException;
import net.orderzone.idcard.exception.ResourceNotFoundException;
import net.orderzone.idcard.mapper.TemplateMapper;
import net.orderzone.idcard.model.Template;
import net.orderzone.idcard.repository.TemplateRepository;
import net.orderzone.idcard.service.impl.TemplateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TemplateService Unit Tests")
class TemplateServiceTest {

    @Mock private TemplateRepository templateRepository;
    @Mock private TemplateMapper     templateMapper;

    @InjectMocks
    private TemplateServiceImpl templateService;

    private TemplateRequestDTO  requestDTO;
    private Template            templateEntity;
    private TemplateResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = TemplateRequestDTO.builder()
                .code("NAVY_CARD")
                .name("Navy Card")
                .organizationName("Test University")
                .primaryColor("#0f3460")
                .secondaryColor("#e8f4f8")
                .textColor("#ffffff")
                .layout("VERTICAL")
                .tagline("Official ID")
                .build();

        templateEntity = Template.builder()
                .id(1L)
                .code("NAVY_CARD")
                .name("Navy Card")
                .organizationName("Test University")
                .primaryColor("#0f3460")
                .secondaryColor("#e8f4f8")
                .textColor("#ffffff")
                .build();

        responseDTO = TemplateResponseDTO.builder()
                .id(1L)
                .code("NAVY_CARD")
                .name("Navy Card")
                .organizationName("Test University")
                .build();
    }

    @Test
    @DisplayName("createTemplate: should save and return template")
    void createTemplate_Success() {
        when(templateRepository.existsByCode("NAVY_CARD")).thenReturn(false);
        when(templateMapper.toEntity(requestDTO)).thenReturn(templateEntity);
        when(templateRepository.save(templateEntity)).thenReturn(templateEntity);
        when(templateMapper.toResponseDTO(templateEntity)).thenReturn(responseDTO);

        TemplateResponseDTO result = templateService.createTemplate(requestDTO);

        assertThat(result.getCode()).isEqualTo("NAVY_CARD");
        verify(templateRepository).save(templateEntity);
    }

    @Test
    @DisplayName("createTemplate: should throw on duplicate code")
    void createTemplate_DuplicateCode_Throws() {
        when(templateRepository.existsByCode("NAVY_CARD")).thenReturn(true);

        assertThatThrownBy(() -> templateService.createTemplate(requestDTO))
                .isInstanceOf(DuplicateResourceException.class);

        verify(templateRepository, never()).save(any());
    }

    @Test
    @DisplayName("getTemplateById: should return template")
    void getTemplateById_Found() {
        when(templateRepository.findById(1L)).thenReturn(Optional.of(templateEntity));
        when(templateMapper.toResponseDTO(templateEntity)).thenReturn(responseDTO);

        assertThat(templateService.getTemplateById(1L).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getTemplateById: should throw when not found")
    void getTemplateById_NotFound_Throws() {
        when(templateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> templateService.getTemplateById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deleteTemplate: should delete the template")
    void deleteTemplate_Success() {
        when(templateRepository.findById(1L)).thenReturn(Optional.of(templateEntity));

        templateService.deleteTemplate(1L);

        verify(templateRepository).delete(templateEntity);
    }
}
