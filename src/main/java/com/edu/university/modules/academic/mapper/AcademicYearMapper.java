package com.edu.university.modules.academic.mapper;

import com.edu.university.modules.academic.dto.request.AcademicYearRequestDTO;
import com.edu.university.modules.academic.dto.response.AcademicYearResponseDTO;
import com.edu.university.modules.academic.entity.AcademicYear;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AcademicYearMapper {
    AcademicYear toEntity(AcademicYearRequestDTO requestDTO);
    AcademicYearResponseDTO toResponseDTO(AcademicYear academicYear);
    void updateEntityFromDTO(AcademicYearRequestDTO requestDTO, @MappingTarget AcademicYear academicYear);
}
