package com.edu.university.modules.schedule.mapper;

import com.edu.university.modules.schedule.dto.request.ScheduleRequestDTO;
import com.edu.university.modules.schedule.dto.response.ScheduleResponseDTO;
import com.edu.university.modules.schedule.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScheduleMapper {
    @Mapping(target = "courseSection", ignore = true)
    @Mapping(target = "lecturer", ignore = true)
    @Mapping(target = "room", ignore = true)
    Schedule toEntity(ScheduleRequestDTO requestDTO);

    @Mapping(target = "courseSectionId", source = "courseSection.id")
    @Mapping(target = "courseName", source = "courseSection.course.name")
    @Mapping(target = "classCode", source = "courseSection.classCode")
    @Mapping(target = "lecturerId", source = "lecturer.id")
    @Mapping(target = "lecturerUsername", source = "lecturer.username")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomName", source = "room.roomName")
    ScheduleResponseDTO toResponseDTO(Schedule schedule);

    @Mapping(target = "courseSection", ignore = true)
    @Mapping(target = "lecturer", ignore = true)
    @Mapping(target = "room", ignore = true)
    void updateEntityFromDTO(ScheduleRequestDTO requestDTO, @MappingTarget Schedule schedule);
}
