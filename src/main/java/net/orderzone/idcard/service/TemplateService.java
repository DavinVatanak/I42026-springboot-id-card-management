package net.orderzone.idcard.service;

import net.orderzone.idcard.dto.TemplateRequestDTO;
import net.orderzone.idcard.dto.TemplateResponseDTO;

import java.util.List;

public interface TemplateService {

    TemplateResponseDTO createTemplate(TemplateRequestDTO request);

    TemplateResponseDTO getTemplateById(Long id);

    TemplateResponseDTO getTemplateByCode(String code);

    List<TemplateResponseDTO> getAllTemplates();

    TemplateResponseDTO updateTemplate(Long id, TemplateRequestDTO request);

    void deleteTemplate(Long id);
}
