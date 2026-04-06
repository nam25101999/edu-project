package com.edu.university.modules.grading.mapper;

import com.edu.university.modules.grading.dto.request.GradeScaleRequestDTO;
import com.edu.university.modules.grading.dto.response.GradeScaleResponseDTO;
import com.edu.university.modules.grading.entity.GradeScale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GradeScaleMapper {
    GradeScale toEntity(GradeScaleRequestDTO requestDTO);
    GradeScaleResponseDTO toResponseDTO(GradeScale gradeScale);
    void updateEntityFromDTO(GradeScaleRequestDTO requestDTO, @MappingTarget GradeScale gradeScale);
}
