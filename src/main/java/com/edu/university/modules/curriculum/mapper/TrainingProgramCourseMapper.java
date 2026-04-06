package com.edu.university.modules.curriculum.mapper;

import com.edu.university.modules.curriculum.dto.request.TrainingProgramCourseRequestDTO;
import com.edu.university.modules.curriculum.dto.response.TrainingProgramCourseResponseDTO;
import com.edu.university.modules.curriculum.entity.TrainingProgramCourse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/*
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TrainingProgramCourseMapper {

    @Mapping(target = "trainingProgram", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "prerequisiteCourse", ignore = true)
    @Mapping(target = "required", ignore = true)
    public abstract TrainingProgramCourse toEntity(TrainingProgramCourseRequestDTO requestDTO);

    @Mapping(target = "trainingProgramId", source = "trainingProgram.id")
    @Mapping(target = "programName", source = "trainingProgram.programName")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseCode", source = "course.code")
    @Mapping(target = "courseName", source = "course.name")
    @Mapping(target = "credits", source = "course.credits")
    @Mapping(target = "isRequired", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "prerequisiteCourseId", source = "prerequisiteCourse.id")
    @Mapping(target = "prerequisiteCourseName", source = "prerequisiteCourse.name")
    public abstract TrainingProgramCourseResponseDTO toResponseDTO(TrainingProgramCourse trainingProgramCourse);

    @Mapping(target = "trainingProgram", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "prerequisiteCourse", ignore = true)
    @Mapping(target = "required", ignore = true)
    public abstract void updateEntityFromDTO(TrainingProgramCourseRequestDTO requestDTO, @MappingTarget TrainingProgramCourse trainingProgramCourse);

    @AfterMapping
    protected void afterMapping(TrainingProgramCourseRequestDTO requestDTO, @MappingTarget TrainingProgramCourse entity) {
        if (requestDTO.getIsRequired() != null) {
            entity.setRequired(requestDTO.getIsRequired());
        }
    }

    @AfterMapping
    protected void afterMapping(TrainingProgramCourse entity, @MappingTarget TrainingProgramCourseResponseDTO dto) {
        dto.setIsRequired(entity.isRequired());
        dto.setIsActive(entity.isActive());
    }
}
*/
