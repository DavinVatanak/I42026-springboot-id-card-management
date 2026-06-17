package net.orderzone.idcard.mapper;

import net.orderzone.idcard.dto.ProfileRequestDTO;
import net.orderzone.idcard.dto.ProfileResponseDTO;
import net.orderzone.idcard.model.Profile;
import net.orderzone.idcard.model.Template;
import org.mapstruct.*;

/**
 * MapStruct mapper for Profile entity ↔ DTOs.
 * The Template association is resolved in the service layer before calling toEntity().
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TemplateMapper.class})
public interface ProfileMapper {

    /**
     * Maps a request DTO to a Profile entity.
     * id, uuid, registrationNumber, createdAt, updatedAt and template are set by the service.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "registrationNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "photoFileName", ignore = true)
    @Mapping(target = "photoContentType", ignore = true)
    @Mapping(target = "template", ignore = true)
    Profile toEntity(ProfileRequestDTO dto);

    /**
     * Maps a Profile entity to a response DTO.
     * The nested Template entity is mapped via TemplateMapper automatically.
     */
    ProfileResponseDTO toResponseDTO(Profile profile);

    /**
     * Partially updates a Profile entity from a request DTO.
     * Null fields in the DTO are ignored — existing values are preserved.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "registrationNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "photoFileName", ignore = true)
    @Mapping(target = "photoContentType", ignore = true)
    @Mapping(target = "template", ignore = true)
    void updateEntityFromDTO(ProfileRequestDTO dto, @MappingTarget Profile profile);
}
