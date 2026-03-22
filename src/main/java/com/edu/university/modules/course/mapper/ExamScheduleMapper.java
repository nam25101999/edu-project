package com.edu.university.modules.course.mapper;

import com.edu.university.modules.course.dto.ExamScheduleDtos.ExamScheduleRequest;
import com.edu.university.modules.course.entity.ExamSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface ExamScheduleMapper {

    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "classSection", ignore = true)
    ExamSchedule toEntity(ExamScheduleRequest request);

    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "classSection", ignore = true)
    void updateEntityFromDto(ExamScheduleRequest request, @MappingTarget ExamSchedule entity);
}