package com.edu.university.modules.system.mapper;

import com.edu.university.modules.system.dto.response.SystemLogResponseDTO;
import com.edu.university.modules.system.entity.SystemLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SystemLogMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    SystemLogResponseDTO toResponseDTO(SystemLog systemLog);
}
