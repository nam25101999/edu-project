package com.edu.university.modules.academic.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.dto.request.StudentCourseSectionRequestDTO;
import com.edu.university.modules.academic.dto.response.StudentCourseSectionResponseDTO;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.StudentCourseSection;
import com.edu.university.modules.academic.mapper.StudentCourseSectionMapper;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.StudentCourseSectionRepository;
import com.edu.university.modules.academic.service.StudentCourseSectionService;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentCourseSectionServiceImpl implements StudentCourseSectionService {

    private final StudentCourseSectionRepository studentCourseSectionRepository;
    private final StudentRepository studentRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final StudentCourseSectionMapper studentCourseSectionMapper;

    @Override
    @Transactional
    public StudentCourseSectionResponseDTO create(StudentCourseSectionRequestDTO requestDTO) {
        if (studentCourseSectionRepository.findByStudentIdAndCourseSectionId(requestDTO.getStudentId(), requestDTO.getCourseSectionId()).isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Sinh viên đã đăng ký lớp học phần này");
        }
        
        StudentCourseSection studentCourseSection = studentCourseSectionMapper.toEntity(requestDTO);
        
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy sinh viên"));
        CourseSection courseSection = courseSectionRepository.findById(requestDTO.getCourseSectionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học phần"));
        
        studentCourseSection.setStudent(student);
        studentCourseSection.setCourseSection(courseSection);
        studentCourseSection.setActive(true);
        studentCourseSection.setCreatedAt(LocalDateTime.now());
        if (studentCourseSection.getRegisteredAt() == null) {
            studentCourseSection.setRegisteredAt(LocalDateTime.now());
        }
        
        return studentCourseSectionMapper.toResponseDTO(studentCourseSectionRepository.save(studentCourseSection));
    }

    @Override
    public List<StudentCourseSectionResponseDTO> getAll() {
        return studentCourseSectionRepository.findAll().stream()
                .map(studentCourseSectionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentCourseSectionResponseDTO getById(UUID id) {
        StudentCourseSection studentCourseSection = studentCourseSectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy thông tin đăng ký"));
        return studentCourseSectionMapper.toResponseDTO(studentCourseSection);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        StudentCourseSection studentCourseSection = studentCourseSectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy thông tin đăng ký"));
        studentCourseSection.softDelete("system");
        studentCourseSectionRepository.save(studentCourseSection);
    }
}
