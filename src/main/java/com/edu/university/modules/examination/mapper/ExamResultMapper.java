package com.edu.university.modules.examination.mapper;

import com.edu.university.modules.examination.dto.request.ExamResultRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamResultResponseDTO;
import com.edu.university.modules.examination.entity.ExamResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExamResultMapper {
    @Mapping(target = "examRegistration", ignore = true)
    @Mapping(target = "gradedBy", ignore = true)
    ExamResult toEntity(ExamResultRequestDTO requestDTO);

    @Mapping(target = "registrationId", source = "examRegistration.id")
    @Mapping(target = "studentName", source = "examRegistration.student.fullName")
    @Mapping(target = "studentCode", source = "examRegistration.student.studentCode")
    @Mapping(target = "gradedById", source = "gradedBy.id")
    @Mapping(target = "gradedByUsername", source = "gradedBy.username")
    @Mapping(target = "isLocked", source = "locked")
    ExamResultResponseDTO toResponseDTO(ExamResult examResult);

    @Mapping(target = "examRegistration", ignore = true)
    @Mapping(target = "gradedBy", ignore = true)
    void updateEntityFromDTO(ExamResultRequestDTO requestDTO, @MappingTarget ExamResult examResult);
}
