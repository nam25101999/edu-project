package com.edu.university.modules.academic.mapper;

import com.edu.university.modules.academic.dto.request.LecturerCourseClassRequestDTO;
import com.edu.university.modules.academic.dto.response.LecturerCourseClassResponseDTO;
import com.edu.university.modules.academic.entity.LecturerCourseClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LecturerCourseClassMapper {
    @Mapping(target = "lecturer", ignore = true)
    @Mapping(target = "courseSection", ignore = true)
    LecturerCourseClass toEntity(LecturerCourseClassRequestDTO requestDTO);

    @Mapping(target = "lecturerId", source = "lecturer.id")
    @Mapping(target = "lecturerUsername", source = "lecturer.username")
    @Mapping(target = "courseSectionId", source = "courseSection.id")
    @Mapping(target = "classCode", source = "courseSection.classCode")
    LecturerCourseClassResponseDTO toResponseDTO(LecturerCourseClass lecturerCourseClass);

    @Mapping(target = "lecturer", ignore = true)
    @Mapping(target = "courseSection", ignore = true)
    void updateEntityFromDTO(LecturerCourseClassRequestDTO requestDTO, @MappingTarget LecturerCourseClass lecturerCourseClass);
}
