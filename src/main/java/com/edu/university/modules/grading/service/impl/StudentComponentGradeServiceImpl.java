package com.edu.university.modules.grading.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.grading.dto.request.StudentComponentGradeRequestDTO;
import com.edu.university.modules.grading.dto.response.StudentComponentGradeResponseDTO;
import com.edu.university.modules.grading.entity.GradeComponent;
import com.edu.university.modules.grading.entity.StudentComponentGrade;
import com.edu.university.modules.grading.mapper.StudentComponentGradeMapper;
import com.edu.university.modules.grading.repository.GradeComponentRepository;
import com.edu.university.modules.grading.repository.StudentComponentGradeRepository;
import com.edu.university.modules.grading.service.StudentComponentGradeService;
import com.edu.university.modules.registration.entity.CourseRegistration;
import com.edu.university.modules.registration.repository.CourseRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentComponentGradeServiceImpl implements StudentComponentGradeService {

    private final StudentComponentGradeRepository studentComponentGradeRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final GradeComponentRepository gradeComponentRepository;
    private final UserRepository userRepository;
    private final StudentComponentGradeMapper studentComponentGradeMapper;

    @Override
    @Transactional
    public StudentComponentGradeResponseDTO upsert(StudentComponentGradeRequestDTO requestDTO) {
        StudentComponentGrade grade = studentComponentGradeRepository
                .findByCourseRegistrationIdAndGradeComponentId(requestDTO.getRegistrationId(), requestDTO.getComponentId())
                .orElse(new StudentComponentGrade());
        
        studentComponentGradeMapper.updateEntityFromDTO(requestDTO, grade);
        
        if (grade.getId() == null) {
            CourseRegistration registration = courseRegistrationRepository.findById(requestDTO.getRegistrationId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));
            GradeComponent component = gradeComponentRepository.findById(requestDTO.getComponentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy thành phần điểm"));
            grade.setCourseRegistration(registration);
            grade.setGradeComponent(component);
            grade.setActive(true);
        }
        
        if (requestDTO.getGradedById() != null) {
            Users grader = userRepository.findById(requestDTO.getGradedById())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            grade.setGradedBy(grader);
        }
        
        return studentComponentGradeMapper.toResponseDTO(studentComponentGradeRepository.save(grade));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentComponentGradeResponseDTO> getByRegistrationId(UUID registrationId, Pageable pageable) {
        return studentComponentGradeRepository.findByCourseRegistrationId(registrationId, pageable)
                .map(studentComponentGradeMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        StudentComponentGrade grade = studentComponentGradeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy điểm thành phần"));
        grade.softDelete("system");
        studentComponentGradeRepository.save(grade);
    }
}
