package com.edu.university.modules.graduation.service.impl;

import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.graduation.dto.request.GraduationResultRequestDTO;
import com.edu.university.modules.graduation.dto.response.GraduationResultResponseDTO;
import com.edu.university.modules.graduation.entity.GraduationCondition;
import com.edu.university.modules.graduation.entity.GraduationResult;
import com.edu.university.modules.graduation.mapper.GraduationResultMapper;
import com.edu.university.modules.graduation.repository.GraduationConditionRepository;
import com.edu.university.modules.graduation.repository.GraduationResultRepository;
import com.edu.university.modules.graduation.service.GraduationResultService;
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
public class GraduationResultServiceImpl implements GraduationResultService {

    private final GraduationResultRepository graduationResultRepository;
    private final StudentRepository studentRepository;
    private final GraduationConditionRepository graduationConditionRepository;
    private final UserRepository userRepository;
    private final GraduationResultMapper graduationResultMapper;

    @Override
    @Transactional
    public GraduationResultResponseDTO create(GraduationResultRequestDTO requestDTO) {
        GraduationResult result = graduationResultMapper.toEntity(requestDTO);
        
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));
        GraduationCondition condition = graduationConditionRepository.findById(requestDTO.getConditionId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điều kiện tốt nghiệp"));
        
        if (requestDTO.getReviewerId() != null) {
            Users reviewer = userRepository.findById(requestDTO.getReviewerId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người xét duyệt"));
            result.setReviewer(reviewer);
        }
        
        result.setStudent(student);
        result.setGraduationCondition(condition);
        result.setActive(true);
        result.setCreatedAt(LocalDateTime.now());
        
        return graduationResultMapper.toResponseDTO(graduationResultRepository.save(result));
    }

    @Override
    public List<GraduationResultResponseDTO> getByStudentId(UUID studentId) {
        return graduationResultRepository.findByStudentId(studentId).stream()
                .map(graduationResultMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        GraduationResult result = graduationResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kết quả tốt nghiệp"));
        result.softDelete("system");
        graduationResultRepository.save(result);
    }
}
