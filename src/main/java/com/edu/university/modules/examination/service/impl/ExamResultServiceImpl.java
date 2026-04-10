package com.edu.university.modules.examination.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.examination.dto.request.ExamResultRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamResultResponseDTO;
import com.edu.university.modules.examination.entity.ExamRegistration;
import com.edu.university.modules.examination.entity.ExamResult;
import com.edu.university.modules.examination.mapper.ExamResultMapper;
import com.edu.university.modules.examination.repository.ExamRegistrationRepository;
import com.edu.university.modules.examination.repository.ExamResultRepository;
import com.edu.university.modules.examination.service.ExamResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExamResultServiceImpl implements ExamResultService {

    private final ExamResultRepository examResultRepository;
    private final ExamRegistrationRepository examRegistrationRepository;
    private final UserRepository userRepository;
    private final ExamResultMapper examResultMapper;

    @Override
    @Transactional
    public ExamResultResponseDTO upsert(ExamResultRequestDTO requestDTO) {
        ExamResult result = examResultRepository.findByExamRegistrationId(requestDTO.getRegistrationId())
                .orElse(new ExamResult());
        
        examResultMapper.updateEntityFromDTO(requestDTO, result);
        result.setLocked(requestDTO.isLocked());
        
        if (result.getId() == null) {
            ExamRegistration registration = examRegistrationRepository.findById(requestDTO.getRegistrationId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy đăng ký thi"));
            result.setExamRegistration(registration);
            result.setActive(true);
        }
        
        if (requestDTO.getGradedById() != null) {
            Users grader = userRepository.findById(requestDTO.getGradedById())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            result.setGradedBy(grader);
        }
        
        return examResultMapper.toResponseDTO(examResultRepository.save(result));
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResultResponseDTO getByRegistrationId(UUID registrationId) {
        ExamResult result = examResultRepository.findByExamRegistrationId(registrationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy kết quả thi"));
        return examResultMapper.toResponseDTO(result);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ExamResult result = examResultRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy kết quả thi"));
        result.softDelete("system");
        examResultRepository.save(result);
    }
}
