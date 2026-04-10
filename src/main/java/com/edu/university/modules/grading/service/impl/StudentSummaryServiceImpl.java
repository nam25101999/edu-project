package com.edu.university.modules.grading.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.grading.dto.request.StudentSummaryRequestDTO;
import com.edu.university.modules.grading.dto.response.StudentSummaryResponseDTO;
import com.edu.university.modules.grading.entity.GradeScale;
import com.edu.university.modules.grading.entity.StudentSummary;
import com.edu.university.modules.grading.mapper.StudentSummaryMapper;
import com.edu.university.modules.grading.repository.GradeScaleRepository;
import com.edu.university.modules.grading.repository.StudentSummaryRepository;
import com.edu.university.modules.grading.service.StudentSummaryService;
import com.edu.university.modules.registration.entity.CourseRegistration;
import com.edu.university.modules.registration.repository.CourseRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentSummaryServiceImpl implements StudentSummaryService {

    private final StudentSummaryRepository studentSummaryRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final GradeScaleRepository gradeScaleRepository;
    private final StudentSummaryMapper studentSummaryMapper;

    @Override
    @Transactional
    public StudentSummaryResponseDTO upsert(StudentSummaryRequestDTO requestDTO) {
        StudentSummary summary = studentSummaryRepository.findByCourseRegistrationId(requestDTO.getRegistrationId())
                .orElse(new StudentSummary());
        
        studentSummaryMapper.updateEntityFromDTO(requestDTO, summary);
        
        if (summary.getId() == null) {
            CourseRegistration registration = courseRegistrationRepository.findById(requestDTO.getRegistrationId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy đăng ký học phần"));
            summary.setCourseRegistration(registration);
            summary.setActive(true);
        }
        
        if (requestDTO.getScaleId() != null) {
            GradeScale scale = gradeScaleRepository.findById(requestDTO.getScaleId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy thang điểm"));
            summary.setGradeScale(scale);
        }
        
        return studentSummaryMapper.toResponseDTO(studentSummaryRepository.save(summary));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentSummaryResponseDTO getByRegistrationId(UUID registrationId) {
        StudentSummary summary = studentSummaryRepository.findByCourseRegistrationId(registrationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy tổng kết điểm"));
        return studentSummaryMapper.toResponseDTO(summary);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        StudentSummary summary = studentSummaryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy tổng kết điểm"));
        summary.softDelete("system");
        studentSummaryRepository.save(summary);
    }
}
