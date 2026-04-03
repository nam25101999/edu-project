package com.edu.university.modules.examination.mapper;

import com.edu.university.modules.examination.dto.request.ExamTypeRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamTypeResponseDTO;
import com.edu.university.modules.examination.entity.ExamType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExamTypeMapper {
    ExamType toEntity(ExamTypeRequestDTO requestDTO);
    ExamTypeResponseDTO toResponseDTO(ExamType examType);
    void updateEntityFromDTO(ExamTypeRequestDTO requestDTO, @MappingTarget ExamType examType);
}
