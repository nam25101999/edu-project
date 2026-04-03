package com.edu.university.modules.schedule.service.impl;

import com.edu.university.modules.schedule.dto.request.RoomRequestDTO;
import com.edu.university.modules.schedule.dto.response.RoomResponseDTO;
import com.edu.university.modules.schedule.entity.Building;
import com.edu.university.modules.schedule.entity.Room;
import com.edu.university.modules.schedule.mapper.RoomMapper;
import com.edu.university.modules.schedule.repository.BuildingRepository;
import com.edu.university.modules.schedule.repository.RoomRepository;
import com.edu.university.modules.schedule.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;
    private final RoomMapper roomMapper;

    @Override
    @Transactional
    public RoomResponseDTO create(RoomRequestDTO requestDTO) {
        if (roomRepository.existsByRoomCode(requestDTO.getRoomCode())) {
            throw new RuntimeException("Mã phòng đã tồn tại");
        }
        Room room = roomMapper.toEntity(requestDTO);
        Building building = buildingRepository.findById(requestDTO.getBuildingId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tòa nhà"));
        room.setBuilding(building);
        room.setActive(true);
        room.setCreatedAt(LocalDateTime.now());
        return roomMapper.toResponseDTO(roomRepository.save(room));
    }

    @Override
    public List<RoomResponseDTO> getAll() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoomResponseDTO getById(UUID id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));
        return roomMapper.toResponseDTO(room);
    }

    @Override
    @Transactional
    public RoomResponseDTO update(UUID id, RoomRequestDTO requestDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));
        roomMapper.updateEntityFromDTO(requestDTO, room);
        Building building = buildingRepository.findById(requestDTO.getBuildingId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tòa nhà"));
        room.setBuilding(building);
        room.setUpdatedAt(LocalDateTime.now());
        return roomMapper.toResponseDTO(roomRepository.save(room));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));
        room.softDelete("system");
        roomRepository.save(room);
    }
}
