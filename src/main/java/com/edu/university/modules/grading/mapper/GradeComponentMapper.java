package com.edu.university.modules.grading.mapper;

import com.edu.university.modules.grading.dto.request.GradeComponentRequestDTO;
import com.edu.university.modules.grading.dto.response.GradeComponentResponseDTO;
import com.edu.university.modules.grading.entity.GradeComponent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GradeComponentMapper {
    @Mapping(target = "courseSection", ignore = true)
    GradeComponent toEntity(GradeComponentRequestDTO requestDTO);

    @Mapping(target = "courseSectionId", source = "courseSection.id")
    @Mapping(target = "classCode", source = "courseSection.classCode")
    GradeComponentResponseDTO toResponseDTO(GradeComponent gradeComponent);

    @Mapping(target = "courseSection", ignore = true)
    void updateEntityFromDTO(GradeComponentRequestDTO requestDTO, @MappingTarget GradeComponent gradeComponent);
}
