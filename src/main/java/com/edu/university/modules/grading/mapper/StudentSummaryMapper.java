package com.edu.university.modules.grading.mapper;

import com.edu.university.modules.grading.dto.request.StudentSummaryRequestDTO;
import com.edu.university.modules.grading.dto.response.StudentSummaryResponseDTO;
import com.edu.university.modules.grading.entity.StudentSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentSummaryMapper {
    @Mapping(target = "courseRegistration", ignore = true)
    @Mapping(target = "gradeScale", ignore = true)
    StudentSummary toEntity(StudentSummaryRequestDTO requestDTO);

    @Mapping(target = "registrationId", source = "courseRegistration.id")
    @Mapping(target = "studentName", source = "courseRegistration.student.fullName")
    @Mapping(target = "studentCode", source = "courseRegistration.student.studentCode")
    @Mapping(target = "courseName", source = "courseRegistration.courseSection.course.name")
    @Mapping(target = "scaleId", source = "gradeScale.id")
    StudentSummaryResponseDTO toResponseDTO(StudentSummary studentSummary);

    @Mapping(target = "courseRegistration", ignore = true)
    @Mapping(target = "gradeScale", ignore = true)
    void updateEntityFromDTO(StudentSummaryRequestDTO requestDTO, @MappingTarget StudentSummary studentSummary);
}
