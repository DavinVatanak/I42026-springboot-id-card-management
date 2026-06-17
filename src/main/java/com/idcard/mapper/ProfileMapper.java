package com.idcard.mapper;

import com.idcard.dto.ProfileRequestDTO;
import com.idcard.dto.ProfileResponseDTO;
import com.idcard.model.Profile;
import org.mapstruct.*;

/**
 * MapStruct mapper for converting between Profile entity and DTOs.
 * Uses componentModel = "spring" to allow Spring DI injection.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfileMapper {

    /**
     * Converts a ProfileRequestDTO to a Profile entity.
     * Fields like id, registrationNumber, createdAt are excluded (set by service/JPA).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationNumber", ignore = true)
    @Mapping(target = "photoPath", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Profile toEntity(ProfileRequestDTO dto);

    /**
     * Converts a Profile entity to a ProfileResponseDTO.
     */
    ProfileResponseDTO toResponseDTO(Profile profile);

    /**
     * Partially updates an existing Profile entity from a request DTO.
     * Null values in the DTO are ignored (IGNORE strategy).
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationNumber", ignore = true)
    @Mapping(target = "photoPath", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ProfileRequestDTO dto, @MappingTarget Profile profile);
}
