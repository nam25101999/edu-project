package com.edu.university.modules.hr.mapper;

import com.edu.university.modules.hr.dto.request.PositionRequestDTO;
import com.edu.university.modules.hr.dto.response.PositionResponseDTO;
import com.edu.university.modules.hr.entity.Position;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PositionMapper {

    @Mapping(target = "department", ignore = true)
    Position toEntity(PositionRequestDTO requestDTO);

    @Mapping(target = "departmentId", source = "department.id")
    PositionResponseDTO toResponseDTO(Position position);

    @Mapping(target = "department", ignore = true)
    void updateEntityFromDTO(PositionRequestDTO requestDTO, @MappingTarget Position position);
}
