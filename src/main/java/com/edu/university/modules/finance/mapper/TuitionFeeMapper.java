package com.edu.university.modules.finance.mapper;

import com.edu.university.modules.finance.dto.request.TuitionFeeRequestDTO;
import com.edu.university.modules.finance.dto.response.TuitionFeeResponseDTO;
import com.edu.university.modules.finance.entity.TuitionFee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TuitionFeeMapper {
    @Mapping(target = "trainingProgram", ignore = true)
    TuitionFee toEntity(TuitionFeeRequestDTO requestDTO);

    @Mapping(target = "trainingProgramId", source = "trainingProgram.id")
    @Mapping(target = "trainingProgramName", source = "trainingProgram.programName")
    TuitionFeeResponseDTO toResponseDTO(TuitionFee tuitionFee);

    @Mapping(target = "trainingProgram", ignore = true)
    void updateEntityFromDTO(TuitionFeeRequestDTO requestDTO, @MappingTarget TuitionFee tuitionFee);
}
