package com.edu.university.modules.system.mapper;

import com.edu.university.modules.system.dto.request.NotificationRequestDTO;
import com.edu.university.modules.system.dto.response.NotificationResponseDTO;
import com.edu.university.modules.system.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {
    @Mapping(target = "targetRole", ignore = true)
    Notification toEntity(NotificationRequestDTO requestDTO);

    @Mapping(target = "targetRoleId", source = "targetRole.id")
    @Mapping(target = "targetRoleName", source = "targetRole.name")
    NotificationResponseDTO toResponseDTO(Notification notification);

    @Mapping(target = "targetRole", ignore = true)
    void updateEntityFromDTO(NotificationRequestDTO requestDTO, @MappingTarget Notification notification);
}
