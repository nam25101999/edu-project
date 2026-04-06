package com.edu.university.modules.student.mapper;

import com.edu.university.modules.student.dto.request.AdvisorClassSectionRequestDTO;
import com.edu.university.modules.student.dto.response.AdvisorClassSectionResponseDTO;
import com.edu.university.modules.student.entity.AdvisorClassSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdvisorClassSectionMapper {

    @Mapping(target = "advisor", ignore = true)
    @Mapping(target = "studentClass", ignore = true)
    AdvisorClassSection toEntity(AdvisorClassSectionRequestDTO requestDTO);

    @Mapping(target = "advisorId", source = "advisor.id")
    @Mapping(target = "advisorName", source = "advisor.fullName")
    @Mapping(target = "studentClassesId", source = "studentClass.id")
    @Mapping(target = "className", source = "studentClass.className")
    @Mapping(target = "startDate", expression = "java(entity.getStartDate() != null ? entity.getStartDate().toLocalDate() : null)")
    @Mapping(target = "endDate", expression = "java(entity.getEndDate() != null ? entity.getEndDate().toLocalDate() : null)")
    AdvisorClassSectionResponseDTO toResponseDTO(AdvisorClassSection entity);

    @Mapping(target = "advisor", ignore = true)
    @Mapping(target = "studentClass", ignore = true)
    void updateEntityFromDTO(AdvisorClassSectionRequestDTO requestDTO, @MappingTarget AdvisorClassSection entity);
}