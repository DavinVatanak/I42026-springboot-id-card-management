package com.idcard.service;

import com.idcard.dto.TemplateRequestDTO;
import com.idcard.dto.TemplateResponseDTO;

import java.util.List;

/**
 * Service interface for ID Card Template operations.
 */
public interface TemplateService {

    TemplateResponseDTO createTemplate(TemplateRequestDTO request);

    TemplateResponseDTO getTemplateById(Long id);

    List<TemplateResponseDTO> getAllTemplates();

    List<TemplateResponseDTO> getActiveTemplates();

    TemplateResponseDTO updateTemplate(Long id, TemplateRequestDTO request);

    void deleteTemplate(Long id);
}
