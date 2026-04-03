package com.edu.university.modules.system.mapper;

import com.edu.university.modules.system.dto.request.SystemSettingRequestDTO;
import com.edu.university.modules.system.dto.response.SystemSettingResponseDTO;
import com.edu.university.modules.system.entity.SystemSetting;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SystemSettingMapper {
    SystemSetting toEntity(SystemSettingRequestDTO requestDTO);
    SystemSettingResponseDTO toResponseDTO(SystemSetting systemSetting);
    void updateEntityFromDTO(SystemSettingRequestDTO requestDTO, @MappingTarget SystemSetting systemSetting);
}
