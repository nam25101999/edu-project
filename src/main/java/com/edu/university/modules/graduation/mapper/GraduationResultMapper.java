package com.edu.university.modules.graduation.mapper;

import com.edu.university.modules.graduation.dto.request.GraduationResultRequestDTO;
import com.edu.university.modules.graduation.dto.response.GraduationResultResponseDTO;
import com.edu.university.modules.graduation.entity.GraduationResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GraduationResultMapper {
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "graduationCondition", ignore = true)
    @Mapping(target = "reviewer", ignore = true)
    GraduationResult toEntity(GraduationResultRequestDTO requestDTO);

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    @Mapping(target = "studentCode", source = "student.studentCode")
    @Mapping(target = "conditionId", source = "graduationCondition.id")
    @Mapping(target = "reviewerId", source = "reviewer.id")
    @Mapping(target = "reviewerUsername", source = "reviewer.username")
    GraduationResultResponseDTO toResponseDTO(GraduationResult graduationResult);

    @Mapping(target = "student", ignore = true)
    @Mapping(target = "graduationCondition", ignore = true)
    @Mapping(target = "reviewer", ignore = true)
    void updateEntityFromDTO(GraduationResultRequestDTO requestDTO, @MappingTarget GraduationResult graduationResult);
}
