package com.edu.university.modules.schedule.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.schedule.dto.request.BuildingRequestDTO;
import com.edu.university.modules.schedule.dto.response.BuildingResponseDTO;
import com.edu.university.modules.schedule.entity.Building;
import com.edu.university.modules.schedule.mapper.BuildingMapper;
import com.edu.university.modules.schedule.repository.BuildingRepository;
import com.edu.university.modules.schedule.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;

    @Override
    @Transactional
    public BuildingResponseDTO create(BuildingRequestDTO requestDTO) {
        if (buildingRepository.existsByBuildingCode(requestDTO.getBuildingCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã tòa nhà đã tồn tại");
        }
        Building building = buildingMapper.toEntity(requestDTO);
        building.setActive(true);
        return buildingMapper.toResponseDTO(buildingRepository.save(building));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BuildingResponseDTO> getAll(Pageable pageable) {
        return buildingRepository.findAll(pageable)
                .map(buildingMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public BuildingResponseDTO getById(UUID id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy tòa nhà"));
        return buildingMapper.toResponseDTO(building);
    }

    @Override
    @Transactional
    public BuildingResponseDTO update(UUID id, BuildingRequestDTO requestDTO) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy tòa nhà"));
        buildingMapper.updateEntityFromDTO(requestDTO, building);
        return buildingMapper.toResponseDTO(buildingRepository.save(building));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy tòa nhà"));
        building.softDelete("system");
        buildingRepository.save(building);
    }
}
