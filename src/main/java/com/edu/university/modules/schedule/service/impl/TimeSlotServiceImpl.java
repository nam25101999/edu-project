package com.edu.university.modules.schedule.service.impl;

import com.edu.university.modules.schedule.dto.request.TimeSlotRequestDTO;
import com.edu.university.modules.schedule.dto.response.TimeSlotResponseDTO;
import com.edu.university.modules.schedule.entity.TimeSlot;
import com.edu.university.modules.schedule.mapper.TimeSlotMapper;
import com.edu.university.modules.schedule.repository.TimeSlotRepository;
import com.edu.university.modules.schedule.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final TimeSlotMapper timeSlotMapper;

    @Override
    @Transactional
    public TimeSlotResponseDTO create(TimeSlotRequestDTO requestDTO) {
        if (timeSlotRepository.existsBySlotCode(requestDTO.getSlotCode())) {
            throw new RuntimeException("Mã ca học đã tồn tại");
        }
        TimeSlot timeSlot = timeSlotMapper.toEntity(requestDTO);
        timeSlot.setActive(true);
        timeSlot.setCreatedAt(LocalDateTime.now());
        return timeSlotMapper.toResponseDTO(timeSlotRepository.save(timeSlot));
    }

    @Override
    public List<TimeSlotResponseDTO> getAll() {
        return timeSlotRepository.findAll().stream()
                .map(timeSlotMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TimeSlotResponseDTO getById(UUID id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca học"));
        return timeSlotMapper.toResponseDTO(timeSlot);
    }

    @Override
    @Transactional
    public TimeSlotResponseDTO update(UUID id, TimeSlotRequestDTO requestDTO) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca học"));
        timeSlotMapper.updateEntityFromDTO(requestDTO, timeSlot);
        timeSlot.setUpdatedAt(LocalDateTime.now());
        return timeSlotMapper.toResponseDTO(timeSlotRepository.save(timeSlot));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        TimeSlot timeSlot = timeSlotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca học"));
        timeSlot.softDelete("system");
        timeSlotRepository.save(timeSlot);
    }
}
