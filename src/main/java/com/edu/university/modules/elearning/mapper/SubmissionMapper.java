package com.edu.university.modules.elearning.mapper;

import com.edu.university.modules.elearning.dto.response.SubmissionResponseDTO;
import com.edu.university.modules.elearning.entity.Submission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {

    @Mapping(source = "assignment.id", target = "assignmentId")
    @Mapping(source = "assignment.title", target = "assignmentTitle")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.fullName", target = "studentName")
    @Mapping(source = "student.studentCode", target = "studentCode")
    SubmissionResponseDTO toResponseDTO(Submission submission);
}
