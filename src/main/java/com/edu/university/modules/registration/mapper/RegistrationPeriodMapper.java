package com.edu.university.modules.registration.mapper;

import com.edu.university.modules.registration.dto.request.RegistrationPeriodRequestDTO;
import com.edu.university.modules.registration.dto.response.RegistrationPeriodResponseDTO;
import com.edu.university.modules.registration.entity.RegistrationPeriod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RegistrationPeriodMapper {
    @Mapping(target = "semester", ignore = true)
    RegistrationPeriod toEntity(RegistrationPeriodRequestDTO requestDTO);

    @Mapping(target = "semesterId", source = "semester.id")
    @Mapping(target = "semesterName", source = "semester.semesterName")
    RegistrationPeriodResponseDTO toResponseDTO(RegistrationPeriod registrationPeriod);

    @Mapping(target = "semester", ignore = true)
    void updateEntityFromDTO(RegistrationPeriodRequestDTO requestDTO, @MappingTarget RegistrationPeriod registrationPeriod);
}
