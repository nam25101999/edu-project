package com.edu.university.modules.academic.mapper;

import com.edu.university.modules.academic.dto.request.CourseSectionRequestDTO;
import com.edu.university.modules.academic.dto.response.CourseSectionResponseDTO;
import com.edu.university.modules.academic.entity.CourseSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseSectionMapper {
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "semester", ignore = true)
    @Mapping(target = "major", ignore = true)
    CourseSection toEntity(CourseSectionRequestDTO requestDTO);

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseName", source = "course.name")
    @Mapping(target = "semesterId", source = "semester.id")
    @Mapping(target = "semesterName", source = "semester.semesterName")
    @Mapping(target = "majorId", source = "major.id")
    @Mapping(target = "majorName", source = "major.name")
    CourseSectionResponseDTO toResponseDTO(CourseSection courseSection);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "semester", ignore = true)
    @Mapping(target = "major", ignore = true)
    void updateEntityFromDTO(CourseSectionRequestDTO requestDTO, @MappingTarget CourseSection courseSection);
}
