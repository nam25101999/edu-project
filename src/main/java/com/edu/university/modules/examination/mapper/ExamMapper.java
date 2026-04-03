package com.edu.university.modules.examination.mapper;

import com.edu.university.modules.examination.dto.request.ExamRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamResponseDTO;
import com.edu.university.modules.examination.entity.Exam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExamMapper {
    @Mapping(target = "examType", ignore = true)
    @Mapping(target = "courseClass", ignore = true)
    @Mapping(target = "semester", ignore = true)
    Exam toEntity(ExamRequestDTO requestDTO);

    @Mapping(target = "examTypeId", source = "examType.id")
    @Mapping(target = "examTypeName", source = "examType.name")
    @Mapping(target = "courseClassId", source = "courseClass.id")
    @Mapping(target = "courseClassName", source = "courseClass.name")
    @Mapping(target = "semesterId", source = "semester.id")
    @Mapping(target = "semesterName", source = "semester.semesterName")
    ExamResponseDTO toResponseDTO(Exam exam);

    @Mapping(target = "examType", ignore = true)
    @Mapping(target = "courseClass", ignore = true)
    @Mapping(target = "semester", ignore = true)
    void updateEntityFromDTO(ExamRequestDTO requestDTO, @MappingTarget Exam exam);
}
