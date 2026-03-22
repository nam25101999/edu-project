package com.edu.university.modules.course.mapper;

import com.edu.university.modules.course.dto.ClassSectionDtos.ClassSectionRequest;
import com.edu.university.modules.course.entity.ClassSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface ClassSectionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    ClassSection toEntity(ClassSectionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    void updateEntityFromDto(ClassSectionRequest request, @MappingTarget ClassSection entity);
}