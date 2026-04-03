package com.edu.university.modules.system.mapper;

import com.edu.university.modules.system.dto.request.UserNotificationRequestDTO;
import com.edu.university.modules.system.dto.response.UserNotificationResponseDTO;
import com.edu.university.modules.system.entity.UserNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserNotificationMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "notification", ignore = true)
    UserNotification toEntity(UserNotificationRequestDTO requestDTO);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "notificationId", source = "notification.id")
    @Mapping(target = "notificationTitle", source = "notification.title")
    UserNotificationResponseDTO toResponseDTO(UserNotification userNotification);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "notification", ignore = true)
    void updateEntityFromDTO(UserNotificationRequestDTO requestDTO, @MappingTarget UserNotification userNotification);
}
