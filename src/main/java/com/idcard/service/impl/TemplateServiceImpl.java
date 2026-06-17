package com.idcard.service.impl;

import com.idcard.dto.TemplateRequestDTO;
import com.idcard.dto.TemplateResponseDTO;
import com.idcard.exception.DuplicateResourceException;
import com.idcard.exception.ResourceNotFoundException;
import com.idcard.mapper.TemplateMapper;
import com.idcard.model.Template;
import com.idcard.repository.TemplateRepository;
import com.idcard.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TemplateService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateMapper templateMapper;

    @Override
    @Transactional
    public TemplateResponseDTO createTemplate(TemplateRequestDTO request) {
        log.info("Creating template: {}", request.getName());

        if (templateRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Template", "name", request.getName());
        }

        Template template = templateMapper.toEntity(request);
        Template saved = templateRepository.save(template);
        return templateMapper.toResponseDTO(saved);
    }

    @Override
    public TemplateResponseDTO getTemplateById(Long id) {
        return templateMapper.toResponseDTO(findOrThrow(id));
    }

    @Override
    public List<TemplateResponseDTO> getAllTemplates() {
        return templateRepository.findAll()
                .stream()
                .map(templateMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TemplateResponseDTO> getActiveTemplates() {
        return templateRepository.findByActiveTrue()
                .stream()
                .map(templateMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TemplateResponseDTO updateTemplate(Long id, TemplateRequestDTO request) {
        Template template = findOrThrow(id);

        // If name changed, check for duplication
        if (!template.getName().equalsIgnoreCase(request.getName())
                && templateRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Template", "name", request.getName());
        }

        templateMapper.updateEntityFromDTO(request, template);
        return templateMapper.toResponseDTO(templateRepository.save(template));
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        templateRepository.delete(findOrThrow(id));
    }

    private Template findOrThrow(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template", "id", id));
    }
}
