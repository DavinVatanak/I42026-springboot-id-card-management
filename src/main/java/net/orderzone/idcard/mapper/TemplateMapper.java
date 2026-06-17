package net.orderzone.idcard.mapper;

import net.orderzone.idcard.dto.TemplateRequestDTO;
import net.orderzone.idcard.dto.TemplateResponseDTO;
import net.orderzone.idcard.model.Template;
import org.mapstruct.*;

/**
 * MapStruct mapper for Template entity ↔ DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TemplateMapper {

    @Mapping(target = "id", ignore = true)
    Template toEntity(TemplateRequestDTO dto);

    TemplateResponseDTO toResponseDTO(Template template);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(TemplateRequestDTO dto, @MappingTarget Template template);
}
