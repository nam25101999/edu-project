package com.edu.university.modules.curriculum.mapper;

import com.edu.university.modules.curriculum.dto.request.TrainingProgramRequestDTO;
import com.edu.university.modules.curriculum.dto.response.TrainingProgramResponseDTO;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainingProgramMapper {
    @Mapping(target = "major", ignore = true)
    @Mapping(target = "department", ignore = true)
    TrainingProgram toEntity(TrainingProgramRequestDTO requestDTO);

    @Mapping(target = "majorId", source = "major.id")
    @Mapping(target = "majorName", source = "major.name")
    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    TrainingProgramResponseDTO toResponseDTO(TrainingProgram trainingProgram);

    @Mapping(target = "major", ignore = true)
    @Mapping(target = "department", ignore = true)
    void updateEntityFromDTO(TrainingProgramRequestDTO requestDTO, @MappingTarget TrainingProgram trainingProgram);
}
