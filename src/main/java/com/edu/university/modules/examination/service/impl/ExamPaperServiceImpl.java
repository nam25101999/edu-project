package com.edu.university.modules.examination.service.impl;

import com.edu.university.modules.examination.dto.request.ExamPaperRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamPaperResponseDTO;
import com.edu.university.modules.examination.entity.Exam;
import com.edu.university.modules.examination.entity.ExamPaper;
import com.edu.university.modules.examination.mapper.ExamPaperMapper;
import com.edu.university.modules.examination.repository.ExamPaperRepository;
import com.edu.university.modules.examination.repository.ExamRepository;
import com.edu.university.modules.examination.service.ExamPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamPaperServiceImpl implements ExamPaperService {

    private final ExamPaperRepository examPaperRepository;
    private final ExamRepository examRepository;
    private final ExamPaperMapper examPaperMapper;

    @Override
    @Transactional
    public ExamPaperResponseDTO create(ExamPaperRequestDTO requestDTO) {
        ExamPaper paper = examPaperMapper.toEntity(requestDTO);
        Exam exam = examRepository.findById(requestDTO.getExamId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kỳ thi"));
        paper.setExam(exam);
        paper.setActive(true);
        paper.setCreatedAt(LocalDateTime.now());
        return examPaperMapper.toResponseDTO(examPaperRepository.save(paper));
    }

    @Override
    public List<ExamPaperResponseDTO> getByExamId(UUID examId) {
        return examPaperRepository.findByExamId(examId).stream()
                .map(examPaperMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ExamPaper paper = examPaperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi"));
        paper.softDelete("system");
        examPaperRepository.save(paper);
    }
}
