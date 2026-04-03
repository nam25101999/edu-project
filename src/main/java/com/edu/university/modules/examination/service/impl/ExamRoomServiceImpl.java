package com.edu.university.modules.examination.service.impl;

import com.edu.university.modules.examination.dto.request.ExamRoomRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamRoomResponseDTO;
import com.edu.university.modules.examination.entity.Exam;
import com.edu.university.modules.examination.entity.ExamRoom;
import com.edu.university.modules.examination.mapper.ExamRoomMapper;
import com.edu.university.modules.examination.repository.ExamRepository;
import com.edu.university.modules.examination.repository.ExamRoomRepository;
import com.edu.university.modules.examination.service.ExamRoomService;
import com.edu.university.modules.schedule.entity.Room;
import com.edu.university.modules.schedule.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamRoomServiceImpl implements ExamRoomService {

    private final ExamRoomRepository examRoomRepository;
    private final ExamRepository examRepository;
    private final RoomRepository roomRepository;
    private final ExamRoomMapper examRoomMapper;

    @Override
    @Transactional
    public ExamRoomResponseDTO create(ExamRoomRequestDTO requestDTO) {
        ExamRoom examRoom = examRoomMapper.toEntity(requestDTO);
        Exam exam = examRepository.findById(requestDTO.getExamId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kỳ thi"));
        Room room = roomRepository.findById(requestDTO.getRoomId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng học"));
        
        examRoom.setExam(exam);
        examRoom.setRoom(room);
        examRoom.setActive(true);
        examRoom.setCreatedAt(LocalDateTime.now());
        return examRoomMapper.toResponseDTO(examRoomRepository.save(examRoom));
    }

    @Override
    public List<ExamRoomResponseDTO> getByExamId(UUID examId) {
        return examRoomRepository.findByExamId(examId).stream()
                .map(examRoomMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ExamRoom examRoom = examRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng thi"));
        examRoom.softDelete("system");
        examRoomRepository.save(examRoom);
    }
}
