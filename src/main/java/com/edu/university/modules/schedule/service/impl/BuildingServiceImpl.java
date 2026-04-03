package com.edu.university.modules.schedule.service.impl;

import com.edu.university.modules.schedule.dto.request.BuildingRequestDTO;
import com.edu.university.modules.schedule.dto.response.BuildingResponseDTO;
import com.edu.university.modules.schedule.entity.Building;
import com.edu.university.modules.schedule.mapper.BuildingMapper;
import com.edu.university.modules.schedule.repository.BuildingRepository;
import com.edu.university.modules.schedule.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;

    @Override
    @Transactional
    public BuildingResponseDTO create(BuildingRequestDTO requestDTO) {
        if (buildingRepository.existsByBuildingCode(requestDTO.getBuildingCode())) {
            throw new RuntimeException("Mã tòa nhà đã tồn tại");
        }
        Building building = buildingMapper.toEntity(requestDTO);
        building.setActive(true);
        building.setCreatedAt(LocalDateTime.now());
        return buildingMapper.toResponseDTO(buildingRepository.save(building));
    }

    @Override
    public List<BuildingResponseDTO> getAll() {
        return buildingRepository.findAll().stream()
                .map(buildingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BuildingResponseDTO getById(UUID id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tòa nhà"));
        return buildingMapper.toResponseDTO(building);
    }

    @Override
    @Transactional
    public BuildingResponseDTO update(UUID id, BuildingRequestDTO requestDTO) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tòa nhà"));
        buildingMapper.updateEntityFromDTO(requestDTO, building);
        building.setUpdatedAt(LocalDateTime.now());
        return buildingMapper.toResponseDTO(buildingRepository.save(building));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tòa nhà"));
        building.softDelete("system");
        buildingRepository.save(building);
    }
}
