package com.edu.university.modules.hr.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.hr.dto.request.PositionRequestDTO;
import com.edu.university.modules.hr.dto.response.PositionResponseDTO;
import com.edu.university.modules.hr.entity.Position;
import com.edu.university.modules.hr.mapper.PositionMapper;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.hr.repository.PositionRepository;
import com.edu.university.modules.hr.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionMapper positionMapper;

    @Override
    @Transactional
    public PositionResponseDTO createPosition(PositionRequestDTO requestDTO) {
        if (positionRepository.existsByCode(requestDTO.getCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã chức vụ đã tồn tại");
        }
        Position position = positionMapper.toEntity(requestDTO);
        if (requestDTO.getDepartmentId() != null) {
            position.setDepartment(departmentRepository.findById(requestDTO.getDepartmentId()).orElse(null));
        }
        position.setActive(true);
        position.setCreatedAt(LocalDateTime.now());
        Position saved = positionRepository.save(position);
        return positionMapper.toResponseDTO(saved);
    }

    @Override
    public List<PositionResponseDTO> getAllPositions() {
        return positionRepository.findAll().stream()
                .map(positionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PositionResponseDTO getPositionById(UUID id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy chức vụ"));
        return positionMapper.toResponseDTO(position);
    }

    @Override
    public PositionResponseDTO getPositionByCode(String code) {
        Position position = positionRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy chức vụ"));
        return positionMapper.toResponseDTO(position);
    }

    @Override
    @Transactional
    public PositionResponseDTO updatePosition(UUID id, PositionRequestDTO requestDTO) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy chức vụ"));
        positionMapper.updateEntityFromDTO(requestDTO, position);
        if (requestDTO.getDepartmentId() != null) {
            position.setDepartment(departmentRepository.findById(requestDTO.getDepartmentId()).orElse(null));
        } else {
            position.setDepartment(null);
        }
        position.setUpdatedAt(LocalDateTime.now());
        return positionMapper.toResponseDTO(positionRepository.save(position));
    }

    @Override
    @Transactional
    public void deletePosition(UUID id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy chức vụ"));
        position.setActive(false);
        position.setDeletedAt(LocalDateTime.now());
        positionRepository.save(position);
    }
}
