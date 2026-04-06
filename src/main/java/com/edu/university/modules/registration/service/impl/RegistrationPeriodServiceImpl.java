package com.edu.university.modules.registration.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.registration.dto.request.RegistrationPeriodRequestDTO;
import com.edu.university.modules.registration.dto.response.RegistrationPeriodResponseDTO;
import com.edu.university.modules.registration.entity.RegistrationPeriod;
import com.edu.university.modules.registration.mapper.RegistrationPeriodMapper;
import com.edu.university.modules.registration.repository.RegistrationPeriodRepository;
import com.edu.university.modules.registration.service.RegistrationPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationPeriodServiceImpl implements RegistrationPeriodService {

    private final RegistrationPeriodRepository registrationPeriodRepository;
    private final SemesterRepository semesterRepository;
    private final RegistrationPeriodMapper registrationPeriodMapper;

    @Override
    @Transactional
    public RegistrationPeriodResponseDTO create(RegistrationPeriodRequestDTO requestDTO) {
        RegistrationPeriod registrationPeriod = registrationPeriodMapper.toEntity(requestDTO);
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy học kỳ"));
        registrationPeriod.setSemester(semester);
        registrationPeriod.setActive(true);
        return registrationPeriodMapper.toResponseDTO(registrationPeriodRepository.save(registrationPeriod));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RegistrationPeriodResponseDTO> getAll(Pageable pageable) {
        return registrationPeriodRepository.findAll(pageable)
                .map(registrationPeriodMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationPeriodResponseDTO getById(UUID id) {
        RegistrationPeriod registrationPeriod = registrationPeriodRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy đợt đăng ký"));
        return registrationPeriodMapper.toResponseDTO(registrationPeriod);
    }

    @Override
    @Transactional
    public RegistrationPeriodResponseDTO update(UUID id, RegistrationPeriodRequestDTO requestDTO) {
        RegistrationPeriod registrationPeriod = registrationPeriodRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy đợt đăng ký"));
        registrationPeriodMapper.updateEntityFromDTO(requestDTO, registrationPeriod);
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy học kỳ"));
        registrationPeriod.setSemester(semester);
        return registrationPeriodMapper.toResponseDTO(registrationPeriodRepository.save(registrationPeriod));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        RegistrationPeriod registrationPeriod = registrationPeriodRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy đợt đăng ký"));
        registrationPeriod.softDelete("system");
        registrationPeriodRepository.save(registrationPeriod);
    }
}
