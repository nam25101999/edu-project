package com.edu.university.modules.registration.mapper;

import com.edu.university.modules.registration.dto.request.EquivalentCourseRequestDTO;
import com.edu.university.modules.registration.dto.response.EquivalentCourseResponseDTO;
import com.edu.university.modules.registration.entity.EquivalentCourse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EquivalentCourseMapper {
    @Mapping(target = "originalCourse", ignore = true)
    @Mapping(target = "equivalentCourse", ignore = true)
    EquivalentCourse toEntity(EquivalentCourseRequestDTO requestDTO);

    @Mapping(target = "originalCourseId", source = "originalCourse.id")
    @Mapping(target = "originalCourseName", source = "originalCourse.name")
    @Mapping(target = "originalCourseCode", source = "originalCourse.courseCode")
    @Mapping(target = "equivalentCourseId", source = "equivalentCourse.id")
    @Mapping(target = "equivalentCourseName", source = "equivalentCourse.name")
    @Mapping(target = "equivalentCourseCode", source = "equivalentCourse.courseCode")
    EquivalentCourseResponseDTO toResponseDTO(EquivalentCourse equivalentCourse);

    @Mapping(target = "originalCourse", ignore = true)
    @Mapping(target = "equivalentCourse", ignore = true)
    void updateEntityFromDTO(EquivalentCourseRequestDTO requestDTO, @MappingTarget EquivalentCourse equivalentCourse);
}
