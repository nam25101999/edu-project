package com.edu.university.modules.grading.mapper;

import com.edu.university.modules.grading.dto.request.StudentComponentGradeRequestDTO;
import com.edu.university.modules.grading.dto.response.StudentComponentGradeResponseDTO;
import com.edu.university.modules.grading.entity.StudentComponentGrade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentComponentGradeMapper {
    @Mapping(target = "courseRegistration", ignore = true)
    @Mapping(target = "gradeComponent", ignore = true)
    @Mapping(target = "gradedBy", ignore = true)
    StudentComponentGrade toEntity(StudentComponentGradeRequestDTO requestDTO);

    @Mapping(target = "registrationId", source = "courseRegistration.id")
    @Mapping(target = "studentName", source = "courseRegistration.student.fullName")
    @Mapping(target = "studentCode", source = "courseRegistration.student.studentCode")
    @Mapping(target = "componentId", source = "gradeComponent.id")
    @Mapping(target = "componentName", source = "gradeComponent.componentName")
    @Mapping(target = "gradedById", source = "gradedBy.id")
    @Mapping(target = "gradedByUsername", source = "gradedBy.username")
    StudentComponentGradeResponseDTO toResponseDTO(StudentComponentGrade studentComponentGrade);

    @Mapping(target = "courseRegistration", ignore = true)
    @Mapping(target = "gradeComponent", ignore = true)
    @Mapping(target = "gradedBy", ignore = true)
    void updateEntityFromDTO(StudentComponentGradeRequestDTO requestDTO, @MappingTarget StudentComponentGrade studentComponentGrade);
}
