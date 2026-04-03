package com.edu.university.modules.examination.mapper;

import com.edu.university.modules.examination.dto.request.ExamRoomRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamRoomResponseDTO;
import com.edu.university.modules.examination.entity.ExamRoom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExamRoomMapper {
    @Mapping(target = "exam", ignore = true)
    @Mapping(target = "room", ignore = true)
    ExamRoom toEntity(ExamRoomRequestDTO requestDTO);

    @Mapping(target = "examId", source = "exam.id")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomName", source = "room.roomName")
    ExamRoomResponseDTO toResponseDTO(ExamRoom examRoom);

    @Mapping(target = "exam", ignore = true)
    @Mapping(target = "room", ignore = true)
    void updateEntityFromDTO(ExamRoomRequestDTO requestDTO, @MappingTarget ExamRoom examRoom);
}
