package com.edu.university.modules.finance.mapper;

import com.edu.university.modules.finance.dto.request.StudentTuitionRequestDTO;
import com.edu.university.modules.finance.dto.response.StudentTuitionResponseDTO;
import com.edu.university.modules.finance.entity.StudentTuition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentTuitionMapper {
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "semester", ignore = true)
    @Mapping(target = "tuitionFee", ignore = true)
    StudentTuition toEntity(StudentTuitionRequestDTO requestDTO);

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    @Mapping(target = "studentCode", source = "student.studentCode")
    @Mapping(target = "semesterId", source = "semester.id")
    @Mapping(target = "semesterName", source = "semester.semesterName")
    @Mapping(target = "tuitionFeeId", source = "tuitionFee.id")
    StudentTuitionResponseDTO toResponseDTO(StudentTuition studentTuition);

    @Mapping(target = "student", ignore = true)
    @Mapping(target = "semester", ignore = true)
    @Mapping(target = "tuitionFee", ignore = true)
    void updateEntityFromDTO(StudentTuitionRequestDTO requestDTO, @MappingTarget StudentTuition studentTuition);
}
