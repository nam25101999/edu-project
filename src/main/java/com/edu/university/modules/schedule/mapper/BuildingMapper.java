package com.edu.university.modules.schedule.mapper;

import com.edu.university.modules.schedule.dto.request.BuildingRequestDTO;
import com.edu.university.modules.schedule.dto.response.BuildingResponseDTO;
import com.edu.university.modules.schedule.entity.Building;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BuildingMapper {
    Building toEntity(BuildingRequestDTO requestDTO);
    BuildingResponseDTO toResponseDTO(Building building);
    void updateEntityFromDTO(BuildingRequestDTO requestDTO, @MappingTarget Building building);
}
