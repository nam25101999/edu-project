package com.edu.university.modules.curriculum.mapper;

import com.edu.university.modules.curriculum.dto.request.TrainingProgramCourseRequestDTO;
import com.edu.university.modules.curriculum.dto.response.TrainingProgramCourseResponseDTO;
import com.edu.university.modules.curriculum.entity.TrainingProgramCourse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainingProgramCourseMapper {
    @Mapping(target = "trainingProgram", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "prerequisiteCourse", ignore = true)
    TrainingProgramCourse toEntity(TrainingProgramCourseRequestDTO requestDTO);

    @Mapping(target = "trainingProgramId", source = "trainingProgram.id")
    @Mapping(target = "programName", source = "trainingProgram.programName")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "prerequisiteCourseId", source = "prerequisiteCourse.id")
    @Mapping(target = "prerequisiteCourseName", source = "prerequisiteCourse.name")
    TrainingProgramCourseResponseDTO toResponseDTO(TrainingProgramCourse trainingProgramCourse);

    @Mapping(target = "trainingProgram", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "prerequisiteCourse", ignore = true)
    void updateEntityFromDTO(TrainingProgramCourseRequestDTO requestDTO, @MappingTarget TrainingProgramCourse trainingProgramCourse);
}
