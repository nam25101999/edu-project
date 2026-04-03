package com.edu.university.modules.grading.service.impl;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        StudentComponentGrade grade = studentComponentGradeRepository.findAll().stream()
                .filter(g -> g.getCourseRegistration().getId().equals(requestDTO.getRegistrationId()) 
                        && g.getGradeComponent().getId().equals(requestDTO.getComponentId()))
                .findFirst()
                .orElse(new StudentComponentGrade());
        
        studentComponentGradeMapper.updateEntityFromDTO(requestDTO, grade);
        
        if (grade.getId() == null) {
            CourseRegistration registration = courseRegistrationRepository.findById(requestDTO.getRegistrationId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đăng ký học phần"));
            GradeComponent component = gradeComponentRepository.findById(requestDTO.getComponentId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thành phần điểm"));
            grade.setCourseRegistration(registration);
            grade.setGradeComponent(component);
            grade.setActive(true);
            grade.setCreatedAt(LocalDateTime.now());
        } else {
            grade.setUpdatedAt(LocalDateTime.now());
        }
        
        if (requestDTO.getGradedById() != null) {
            Users grader = userRepository.findById(requestDTO.getGradedById())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người nhập điểm"));
            grade.setGradedBy(grader);
        }
        
        return studentComponentGradeMapper.toResponseDTO(studentComponentGradeRepository.save(grade));
    }

    @Override
    public List<StudentComponentGradeResponseDTO> getByRegistrationId(UUID registrationId) {
        return studentComponentGradeRepository.findByCourseRegistrationId(registrationId).stream()
                .map(studentComponentGradeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        StudentComponentGrade grade = studentComponentGradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điểm thành phần"));
        grade.softDelete("system");
        studentComponentGradeRepository.save(grade);
    }
}
