package com.edu.university.modules.curriculum.mapper;

import com.edu.university.modules.curriculum.dto.request.CourseRequestDTO;
import com.edu.university.modules.curriculum.dto.response.CourseResponseDTO;
import com.edu.university.modules.curriculum.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper {
    @Mapping(target = "department", ignore = true)
    Course toEntity(CourseRequestDTO requestDTO);

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    CourseResponseDTO toResponseDTO(Course course);

    @Mapping(target = "department", ignore = true)
    void updateEntityFromDTO(CourseRequestDTO requestDTO, @MappingTarget Course course);
}
