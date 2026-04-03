package com.edu.university.modules.examination.mapper;

import com.edu.university.modules.examination.dto.request.ExamRegistrationRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamRegistrationResponseDTO;
import com.edu.university.modules.examination.entity.ExamRegistration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExamRegistrationMapper {
    @Mapping(target = "exam", ignore = true)
    @Mapping(target = "examRoom", ignore = true)
    @Mapping(target = "student", ignore = true)
    ExamRegistration toEntity(ExamRegistrationRequestDTO requestDTO);

    @Mapping(target = "examId", source = "exam.id")
    @Mapping(target = "examRoomId", source = "examRoom.id")
    @Mapping(target = "examRoomName", source = "examRoom.room.roomName")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    @Mapping(target = "studentCode", source = "student.studentCode")
    ExamRegistrationResponseDTO toResponseDTO(ExamRegistration examRegistration);

    @Mapping(target = "exam", ignore = true)
    @Mapping(target = "examRoom", ignore = true)
    @Mapping(target = "student", ignore = true)
    void updateEntityFromDTO(ExamRegistrationRequestDTO requestDTO, @MappingTarget ExamRegistration examRegistration);
}
