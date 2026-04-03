package com.edu.university.modules.academic.mapper;

import com.edu.university.modules.academic.dto.request.StudentCourseSectionRequestDTO;
import com.edu.university.modules.academic.dto.response.StudentCourseSectionResponseDTO;
import com.edu.university.modules.academic.entity.StudentCourseSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentCourseSectionMapper {
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "courseSection", ignore = true)
    StudentCourseSection toEntity(StudentCourseSectionRequestDTO requestDTO);

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    @Mapping(target = "studentCode", source = "student.studentCode")
    @Mapping(target = "courseSectionId", source = "courseSection.id")
    @Mapping(target = "classCode", source = "courseSection.classCode")
    StudentCourseSectionResponseDTO toResponseDTO(StudentCourseSection studentCourseSection);

    @Mapping(target = "student", ignore = true)
    @Mapping(target = "courseSection", ignore = true)
    void updateEntityFromDTO(StudentCourseSectionRequestDTO requestDTO, @MappingTarget StudentCourseSection studentCourseSection);
}
