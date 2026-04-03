package com.edu.university.modules.schedule.mapper;

import com.edu.university.modules.schedule.dto.request.TimeSlotRequestDTO;
import com.edu.university.modules.schedule.dto.response.TimeSlotResponseDTO;
import com.edu.university.modules.schedule.entity.TimeSlot;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TimeSlotMapper {
    TimeSlot toEntity(TimeSlotRequestDTO requestDTO);
    TimeSlotResponseDTO toResponseDTO(TimeSlot timeSlot);
    void updateEntityFromDTO(TimeSlotRequestDTO requestDTO, @MappingTarget TimeSlot timeSlot);
}
