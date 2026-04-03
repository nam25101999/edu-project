package com.edu.university.modules.schedule.mapper;

import com.edu.university.modules.schedule.dto.request.RoomRequestDTO;
import com.edu.university.modules.schedule.dto.response.RoomResponseDTO;
import com.edu.university.modules.schedule.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomMapper {
    @Mapping(target = "building", ignore = true)
    Room toEntity(RoomRequestDTO requestDTO);

    @Mapping(target = "buildingId", source = "building.id")
    @Mapping(target = "buildingName", source = "building.buildingName")
    RoomResponseDTO toResponseDTO(Room room);

    @Mapping(target = "building", ignore = true)
    void updateEntityFromDTO(RoomRequestDTO requestDTO, @MappingTarget Room room);
}
