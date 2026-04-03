package com.edu.university.modules.examination.mapper;

import com.edu.university.modules.examination.dto.request.ExamPaperRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamPaperResponseDTO;
import com.edu.university.modules.examination.entity.ExamPaper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExamPaperMapper {
    @Mapping(target = "exam", ignore = true)
    ExamPaper toEntity(ExamPaperRequestDTO requestDTO);

    @Mapping(target = "examId", source = "exam.id")
    ExamPaperResponseDTO toResponseDTO(ExamPaper examPaper);

    @Mapping(target = "exam", ignore = true)
    void updateEntityFromDTO(ExamPaperRequestDTO requestDTO, @MappingTarget ExamPaper examPaper);
}
