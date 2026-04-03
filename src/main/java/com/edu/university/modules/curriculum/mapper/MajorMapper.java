package com.edu.university.modules.curriculum.mapper;

import com.edu.university.modules.curriculum.dto.request.MajorRequestDTO;
import com.edu.university.modules.curriculum.dto.response.MajorResponseDTO;
import com.edu.university.modules.curriculum.entity.Major;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MajorMapper {
    @Mapping(target = "faculty", ignore = true)
    Major toEntity(MajorRequestDTO requestDTO);

    @Mapping(target = "facultyId", source = "faculty.id")
    @Mapping(target = "facultyName", source = "faculty.name")
    MajorResponseDTO toResponseDTO(Major major);

    @Mapping(target = "faculty", ignore = true)
    void updateEntityFromDTO(MajorRequestDTO requestDTO, @MappingTarget Major major);
}
