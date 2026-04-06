package com.edu.university.modules.schedule.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.schedule.dto.request.TimeSlotRequestDTO;
import com.edu.university.modules.schedule.dto.response.TimeSlotResponseDTO;
import com.edu.university.modules.schedule.entity.TimeSlot;
import com.edu.university.modules.schedule.mapper.TimeSlotMapper;
import com.edu.university.modules.schedule.repository.TimeSlotRepository;
import com.edu.university.modules.schedule.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final TimeSlotMapper timeSlotMapper;

    @Override
    @Transactional
    public TimeSlotResponseDTO create(TimeSlotRequestDTO requestDTO) {
        if (timeSlotRepository.existsBySlotCode(requestDTO.getSlotCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã ca học đã tồn tại");
        }
        TimeSlot timeSlot = timeSlotMapper.toEntity(requestDTO);
        timeSlot.setActive(true);
        return timeSlotMapper.toResponseDTO(timeSlotRepository.save(timeSlot));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TimeSlotResponseDTO> getAll(Pageable pageable) {
        return timeSlotRepository.findAll(pageable)
                .map(timeSlotMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public TimeSlotResponseDTO getById(UUID id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy ca học"));
        return timeSlotMapper.toResponseDTO(timeSlot);
    }

    @Override
    @Transactional
    public TimeSlotResponseDTO update(UUID id, TimeSlotRequestDTO requestDTO) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy ca học"));
        timeSlotMapper.updateEntityFromDTO(requestDTO, timeSlot);
        return timeSlotMapper.toResponseDTO(timeSlotRepository.save(timeSlot));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy ca học"));
        timeSlot.softDelete("system");
        timeSlotRepository.save(timeSlot);
    }
}
