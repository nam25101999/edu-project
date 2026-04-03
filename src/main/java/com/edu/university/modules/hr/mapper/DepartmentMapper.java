package com.edu.university.modules.hr.mapper;

import com.edu.university.modules.hr.dto.request.DepartmentRequestDTO;
import com.edu.university.modules.hr.dto.response.DepartmentResponseDTO;
import com.edu.university.modules.hr.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepartmentMapper {

    Department toEntity(DepartmentRequestDTO requestDTO);

    DepartmentResponseDTO toResponseDTO(Department department);

    void updateEntityFromDTO(DepartmentRequestDTO requestDTO, @MappingTarget Department department);
}
