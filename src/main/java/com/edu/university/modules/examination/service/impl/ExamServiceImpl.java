package com.edu.university.modules.examination.service.impl;
 
import com.edu.university.common.exception.AppException;
import com.edu.university.common.exception.ErrorCode;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.UUID;
 
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
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        Course course = courseRepository.findById(requestDTO.getCourseClassId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_NOT_FOUND));
        
        exam.setExamType(type);
        exam.setCourseClass(course);
        exam.setSemester(semester);
        exam.setActive(true);
        
        return examMapper.toResponseDTO(examRepository.save(exam));
    }
 
    @Override
    @Transactional(readOnly = true)
    public Page<ExamResponseDTO> getAll(Pageable pageable) {
        return examRepository.findAll(pageable)
                .map(examMapper::toResponseDTO);
    }
 
    @Override
    @Transactional(readOnly = true)
    public ExamResponseDTO getById(UUID id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        return examMapper.toResponseDTO(exam);
    }
 
    @Override
    @Transactional
    public ExamResponseDTO update(UUID id, ExamRequestDTO requestDTO) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        examMapper.updateEntityFromDTO(requestDTO, exam);
        
        ExamType type = examTypeRepository.findById(requestDTO.getExamTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        Course course = courseRepository.findById(requestDTO.getCourseClassId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new AppException(ErrorCode.SEMESTER_NOT_FOUND));
        
        exam.setExamType(type);
        exam.setCourseClass(course);
        exam.setSemester(semester);
        
        return examMapper.toResponseDTO(examRepository.save(exam));
    }
 
    @Override
    @Transactional
    public void delete(UUID id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        exam.softDelete("system");
        examRepository.save(exam);
    }
}
