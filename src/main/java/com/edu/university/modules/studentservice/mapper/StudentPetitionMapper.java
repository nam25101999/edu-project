package com.edu.university.modules.studentservice.mapper;

import com.edu.university.modules.studentservice.dto.response.StudentPetitionResponseDTO;
import com.edu.university.modules.studentservice.entity.StudentPetition;
import org.springframework.stereotype.Component;

@Component
public class StudentPetitionMapper {

    public StudentPetitionResponseDTO toResponseDTO(StudentPetition petition) {
        if (petition == null) return null;
        return StudentPetitionResponseDTO.builder()
                .id(petition.getId())
                .studentId(petition.getStudent() != null ? petition.getStudent().getId() : null)
                .studentName(petition.getStudent() != null ? petition.getStudent().getFullName() : null)
                .studentCode(petition.getStudent() != null ? petition.getStudent().getStudentCode() : null)
                .title(petition.getTitle())
                .content(petition.getContent())
                .status(petition.getStatus())
                .attachmentUrl(petition.getAttachmentUrl())
                .createdAt(petition.getCreatedAt())
                .responseContent(petition.getResponseContent())
                .build();
    }
}
