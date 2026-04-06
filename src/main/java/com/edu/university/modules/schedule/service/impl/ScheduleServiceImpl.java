package com.edu.university.modules.schedule.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.schedule.dto.request.ScheduleRequestDTO;
import com.edu.university.modules.schedule.dto.response.ScheduleResponseDTO;
import com.edu.university.modules.schedule.entity.Room;
import com.edu.university.modules.schedule.entity.Schedule;
import com.edu.university.modules.schedule.mapper.ScheduleMapper;
import com.edu.university.modules.schedule.repository.RoomRepository;
import com.edu.university.modules.schedule.repository.ScheduleRepository;
import com.edu.university.modules.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ScheduleMapper scheduleMapper;

    @Override
    @Transactional
    public ScheduleResponseDTO create(ScheduleRequestDTO requestDTO) {
        Schedule schedule = scheduleMapper.toEntity(requestDTO);
        
        if (requestDTO.getCourseSectionId() != null) {
            CourseSection courseSection = courseSectionRepository.findById(requestDTO.getCourseSectionId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học phần"));
            schedule.setCourseSection(courseSection);
        }
        
        if (requestDTO.getLecturerId() != null) {
            Users lecturer = userRepository.findById(requestDTO.getLecturerId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy giảng viên"));
            schedule.setLecturer(lecturer);
        }
        
        if (requestDTO.getRoomId() != null) {
            Room room = roomRepository.findById(requestDTO.getRoomId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy phòng học"));
            schedule.setRoom(room);
        }
        
        schedule.setActive(true);
        return scheduleMapper.toResponseDTO(scheduleRepository.save(schedule));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScheduleResponseDTO> getAll(Pageable pageable) {
        return scheduleRepository.findAll(pageable)
                .map(scheduleMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleResponseDTO getById(UUID id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lịch học"));
        return scheduleMapper.toResponseDTO(schedule);
    }

    @Override
    @Transactional
    public ScheduleResponseDTO update(UUID id, ScheduleRequestDTO requestDTO) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lịch học"));
        scheduleMapper.updateEntityFromDTO(requestDTO, schedule);
        
        if (requestDTO.getCourseSectionId() != null) {
            CourseSection courseSection = courseSectionRepository.findById(requestDTO.getCourseSectionId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học phần"));
            schedule.setCourseSection(courseSection);
        }
        
        if (requestDTO.getLecturerId() != null) {
            Users lecturer = userRepository.findById(requestDTO.getLecturerId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy giảng viên"));
            schedule.setLecturer(lecturer);
        }
        
        if (requestDTO.getRoomId() != null) {
            Room room = roomRepository.findById(requestDTO.getRoomId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy phòng học"));
            schedule.setRoom(room);
        }
        
        return scheduleMapper.toResponseDTO(scheduleRepository.save(schedule));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lịch học"));
        schedule.softDelete("system");
        scheduleRepository.save(schedule);
    }
}
