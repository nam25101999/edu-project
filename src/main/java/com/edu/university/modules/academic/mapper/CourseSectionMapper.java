package com.edu.university.modules.academic.mapper;

import com.edu.university.modules.academic.dto.request.CourseSectionRequestDTO;
import com.edu.university.modules.academic.dto.response.CourseSectionResponseDTO;
import com.edu.university.modules.academic.entity.CourseSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseSectionMapper {
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "semester", ignore = true)
    CourseSection toEntity(CourseSectionRequestDTO requestDTO);

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    @Mapping(target = "semesterId", source = "semester.id")
    @Mapping(target = "semesterName", source = "semester.semesterName")
    CourseSectionResponseDTO toResponseDTO(CourseSection courseSection);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "semester", ignore = true)
    void updateEntityFromDTO(CourseSectionRequestDTO requestDTO, @MappingTarget CourseSection courseSection);
}
