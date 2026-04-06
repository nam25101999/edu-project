package com.edu.university.modules.schedule.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.schedule.dto.request.RoomRequestDTO;
import com.edu.university.modules.schedule.dto.response.RoomResponseDTO;
import com.edu.university.modules.schedule.entity.Building;
import com.edu.university.modules.schedule.entity.Room;
import com.edu.university.modules.schedule.mapper.RoomMapper;
import com.edu.university.modules.schedule.repository.BuildingRepository;
import com.edu.university.modules.schedule.repository.RoomRepository;
import com.edu.university.modules.schedule.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã phòng đã tồn tại");
        }
        Room room = roomMapper.toEntity(requestDTO);
        Building building = buildingRepository.findById(requestDTO.getBuildingId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy tòa nhà"));
        room.setBuilding(building);
        room.setActive(true);
        return roomMapper.toResponseDTO(roomRepository.save(room));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponseDTO> getAll(Pageable pageable) {
        return roomRepository.findAll(pageable)
                .map(roomMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponseDTO getById(UUID id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy phòng"));
        return roomMapper.toResponseDTO(room);
    }

    @Override
    @Transactional
    public RoomResponseDTO update(UUID id, RoomRequestDTO requestDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy phòng"));
        roomMapper.updateEntityFromDTO(requestDTO, room);
        Building building = buildingRepository.findById(requestDTO.getBuildingId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy tòa nhà"));
        room.setBuilding(building);
        return roomMapper.toResponseDTO(roomRepository.save(room));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy phòng"));
        room.softDelete("system");
        roomRepository.save(room);
    }
}
