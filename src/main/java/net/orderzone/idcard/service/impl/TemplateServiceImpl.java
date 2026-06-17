package net.orderzone.idcard.service.impl;

import net.orderzone.idcard.dto.TemplateRequestDTO;
import net.orderzone.idcard.dto.TemplateResponseDTO;
import net.orderzone.idcard.exception.DuplicateResourceException;
import net.orderzone.idcard.exception.ResourceNotFoundException;
import net.orderzone.idcard.mapper.TemplateMapper;
import net.orderzone.idcard.model.Template;
import net.orderzone.idcard.repository.TemplateRepository;
import net.orderzone.idcard.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateMapper     templateMapper;

    @Override
    @Transactional
    public TemplateResponseDTO createTemplate(TemplateRequestDTO request) {
        if (templateRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Template", "code", request.getCode());
        }
        Template saved = templateRepository.save(templateMapper.toEntity(request));
        return templateMapper.toResponseDTO(saved);
    }

    @Override
    public TemplateResponseDTO getTemplateById(Long id) {
        return templateMapper.toResponseDTO(findOrThrow(id));
    }

    @Override
    public TemplateResponseDTO getTemplateByCode(String code) {
        return templateMapper.toResponseDTO(
                templateRepository.findByCode(code)
                        .orElseThrow(() -> new ResourceNotFoundException("Template", "code", code)));
    }

    @Override
    public List<TemplateResponseDTO> getAllTemplates() {
        return templateRepository.findAll().stream()
                .map(templateMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TemplateResponseDTO updateTemplate(Long id, TemplateRequestDTO request) {
        Template template = findOrThrow(id);
        if (!template.getCode().equalsIgnoreCase(request.getCode())
                && templateRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Template", "code", request.getCode());
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
