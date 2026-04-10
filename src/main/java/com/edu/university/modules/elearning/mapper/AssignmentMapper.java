package com.edu.university.modules.elearning.mapper;

import com.edu.university.modules.elearning.dto.response.AssignmentResponseDTO;
import com.edu.university.modules.elearning.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    @Mapping(source = "courseSection.id", target = "courseSectionId")
    @Mapping(source = "courseSection.classCode", target = "classCode")
    AssignmentResponseDTO toResponseDTO(Assignment assignment);
}
