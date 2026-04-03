package com.edu.university.modules.curriculum.mapper;

import com.edu.university.modules.curriculum.dto.request.CoursePrerequisiteRequestDTO;
import com.edu.university.modules.curriculum.dto.response.CoursePrerequisiteResponseDTO;
import com.edu.university.modules.curriculum.entity.CoursePrerequisite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoursePrerequisiteMapper {
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "prerequisiteCourse", ignore = true)
    CoursePrerequisite toEntity(CoursePrerequisiteRequestDTO requestDTO);

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    @Mapping(target = "prerequisiteCourseId", source = "prerequisiteCourse.id")
    @Mapping(target = "prerequisiteCourseName", source = "prerequisiteCourse.name")
    CoursePrerequisiteResponseDTO toResponseDTO(CoursePrerequisite coursePrerequisite);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "prerequisiteCourse", ignore = true)
    void updateEntityFromDTO(CoursePrerequisiteRequestDTO requestDTO, @MappingTarget CoursePrerequisite coursePrerequisite);
}
