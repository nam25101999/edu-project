package com.edu.university.modules.examination.service.impl;

import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.examination.dto.request.ExamRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamResponseDTO;
import com.edu.university.modules.examination.entity.Exam;
import com.edu.university.modules.examination.entity.ExamType;
import com.edu.university.modules.examination.mapper.ExamMapper;
import com.edu.university.modules.examination.repository.ExamRepository;
import com.edu.university.modules.examination.repository.ExamTypeRepository;
import com.edu.university.modules.examination.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamTypeRepository examTypeRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final ExamMapper examMapper;

    @Override
    @Transactional
    public ExamResponseDTO create(ExamRequestDTO requestDTO) {
        Exam exam = examMapper.toEntity(requestDTO);
        
        ExamType type = examTypeRepository.findById(requestDTO.getExamTypeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại kỳ thi"));
        Course course = courseRepository.findById(requestDTO.getCourseClassId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học"));
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ"));
        
        exam.setExamType(type);
        exam.setCourseClass(course);
        exam.setSemester(semester);
        exam.setActive(true);
        exam.setCreatedAt(LocalDateTime.now());
        
        return examMapper.toResponseDTO(examRepository.save(exam));
    }

    @Override
    public List<ExamResponseDTO> getAll() {
        return examRepository.findAll().stream()
                .map(examMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExamResponseDTO getById(UUID id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kỳ thi"));
        return examMapper.toResponseDTO(exam);
    }

    @Override
    @Transactional
    public ExamResponseDTO update(UUID id, ExamRequestDTO requestDTO) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kỳ thi"));
        examMapper.updateEntityFromDTO(requestDTO, exam);
        
        ExamType type = examTypeRepository.findById(requestDTO.getExamTypeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại kỳ thi"));
        Course course = courseRepository.findById(requestDTO.getCourseClassId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học"));
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ"));
        
        exam.setExamType(type);
        exam.setCourseClass(course);
        exam.setSemester(semester);
        exam.setUpdatedAt(LocalDateTime.now());
        
        return examMapper.toResponseDTO(examRepository.save(exam));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kỳ thi"));
        exam.softDelete("system");
        examRepository.save(exam);
    }
}
