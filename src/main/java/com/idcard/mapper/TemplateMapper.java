package com.idcard.mapper;

import com.idcard.dto.TemplateRequestDTO;
import com.idcard.dto.TemplateResponseDTO;
import com.idcard.model.Template;
import org.mapstruct.*;

/**
 * MapStruct mapper for Template entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TemplateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Template toEntity(TemplateRequestDTO dto);

    TemplateResponseDTO toResponseDTO(Template template);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(TemplateRequestDTO dto, @MappingTarget Template template);
}
