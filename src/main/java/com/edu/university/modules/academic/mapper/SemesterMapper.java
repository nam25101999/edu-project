package com.edu.university.modules.academic.mapper;

import com.edu.university.modules.academic.dto.request.SemesterRequestDTO;
import com.edu.university.modules.academic.dto.response.SemesterResponseDTO;
import com.edu.university.modules.academic.entity.Semester;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SemesterMapper {
    Semester toEntity(SemesterRequestDTO requestDTO);
    SemesterResponseDTO toResponseDTO(Semester semester);
    void updateEntityFromDTO(SemesterRequestDTO requestDTO, @MappingTarget Semester semester);
}
