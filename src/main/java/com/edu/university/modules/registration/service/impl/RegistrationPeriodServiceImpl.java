package com.edu.university.modules.registration.service.impl;

import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.registration.dto.request.RegistrationPeriodRequestDTO;
import com.edu.university.modules.registration.dto.response.RegistrationPeriodResponseDTO;
import com.edu.university.modules.registration.entity.RegistrationPeriod;
import com.edu.university.modules.registration.mapper.RegistrationPeriodMapper;
import com.edu.university.modules.registration.repository.RegistrationPeriodRepository;
import com.edu.university.modules.registration.service.RegistrationPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ"));
        registrationPeriod.setSemester(semester);
        registrationPeriod.setActive(true);
        registrationPeriod.setCreatedAt(LocalDateTime.now());
        return registrationPeriodMapper.toResponseDTO(registrationPeriodRepository.save(registrationPeriod));
    }

    @Override
    public List<RegistrationPeriodResponseDTO> getAll() {
        return registrationPeriodRepository.findAll().stream()
                .map(registrationPeriodMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RegistrationPeriodResponseDTO getById(UUID id) {
        RegistrationPeriod registrationPeriod = registrationPeriodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt đăng ký"));
        return registrationPeriodMapper.toResponseDTO(registrationPeriod);
    }

    @Override
    @Transactional
    public RegistrationPeriodResponseDTO update(UUID id, RegistrationPeriodRequestDTO requestDTO) {
        RegistrationPeriod registrationPeriod = registrationPeriodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt đăng ký"));
        registrationPeriodMapper.updateEntityFromDTO(requestDTO, registrationPeriod);
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học kỳ"));
        registrationPeriod.setSemester(semester);
        registrationPeriod.setUpdatedAt(LocalDateTime.now());
        return registrationPeriodMapper.toResponseDTO(registrationPeriodRepository.save(registrationPeriod));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        RegistrationPeriod registrationPeriod = registrationPeriodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt đăng ký"));
        registrationPeriod.softDelete("system");
        registrationPeriodRepository.save(registrationPeriod);
    }
}
