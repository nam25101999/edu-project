package com.edu.university.modules.elearning.mapper;

import com.edu.university.modules.elearning.dto.response.MaterialResponseDTO;
import com.edu.university.modules.elearning.entity.Material;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MaterialMapper {

    @Mapping(source = "courseSection.id", target = "courseSectionId")
    @Mapping(source = "courseSection.classCode", target = "classCode")
    MaterialResponseDTO toResponseDTO(Material material);
}
