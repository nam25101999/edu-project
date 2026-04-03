package com.edu.university.modules.graduation.mapper;

import com.edu.university.modules.graduation.dto.request.GraduationConditionRequestDTO;
import com.edu.university.modules.graduation.dto.response.GraduationConditionResponseDTO;
import com.edu.university.modules.graduation.entity.GraduationCondition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GraduationConditionMapper {
    @Mapping(target = "trainingProgram", ignore = true)
    GraduationCondition toEntity(GraduationConditionRequestDTO requestDTO);

    @Mapping(target = "trainingProgramId", source = "trainingProgram.id")
    @Mapping(target = "trainingProgramName", source = "trainingProgram.programName")
    GraduationConditionResponseDTO toResponseDTO(GraduationCondition graduationCondition);

    @Mapping(target = "trainingProgram", ignore = true)
    void updateEntityFromDTO(GraduationConditionRequestDTO requestDTO, @MappingTarget GraduationCondition graduationCondition);
}
