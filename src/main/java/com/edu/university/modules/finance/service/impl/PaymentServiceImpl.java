package com.edu.university.modules.finance.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.finance.dto.request.PaymentRequestDTO;
import com.edu.university.modules.finance.dto.response.PaymentResponseDTO;
import com.edu.university.modules.finance.entity.Payment;
import com.edu.university.modules.finance.entity.StudentTuition;
import com.edu.university.modules.finance.mapper.PaymentMapper;
import com.edu.university.modules.finance.repository.PaymentRepository;
import com.edu.university.modules.finance.repository.StudentTuitionRepository;
import com.edu.university.modules.finance.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentTuitionRepository studentTuitionRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponseDTO create(PaymentRequestDTO requestDTO) {
        Payment payment = paymentMapper.toEntity(requestDTO);
        
        StudentTuition tuition = studentTuitionRepository.findById(requestDTO.getStudentTuitionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TUITION_FEE_NOT_FOUND));
        
        if (requestDTO.getCashierId() != null) {
            Users cashier = userRepository.findById(requestDTO.getCashierId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            payment.setCashier(cashier);
        }
        
        payment.setStudentTuition(tuition);
        payment.setActive(true);
        payment.setCreatedAt(LocalDateTime.now());
        
        return paymentMapper.toResponseDTO(paymentRepository.save(payment));
    }

    @Override
    public Page<PaymentResponseDTO> getByStudentTuitionId(UUID studentTuitionId, Pageable pageable) {
        return paymentRepository.findByStudentTuitionId(studentTuitionId, pageable)
                .map(paymentMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy khoản thanh toán"));
        payment.softDelete("system");
        paymentRepository.save(payment);
    }
}
