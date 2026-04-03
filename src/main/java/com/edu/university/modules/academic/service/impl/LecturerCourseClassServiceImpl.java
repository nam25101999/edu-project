package com.edu.university.modules.academic.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.dto.request.LecturerCourseClassRequestDTO;
import com.edu.university.modules.academic.dto.response.LecturerCourseClassResponseDTO;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.LecturerCourseClass;
import com.edu.university.modules.academic.mapper.LecturerCourseClassMapper;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.LecturerCourseClassRepository;
import com.edu.university.modules.academic.service.LecturerCourseClassService;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LecturerCourseClassServiceImpl implements LecturerCourseClassService {

    private final LecturerCourseClassRepository lecturerCourseClassRepository;
    private final UserRepository userRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final LecturerCourseClassMapper lecturerCourseClassMapper;

    @Override
    @Transactional
    public LecturerCourseClassResponseDTO create(LecturerCourseClassRequestDTO requestDTO) {
        LecturerCourseClass lecturerCourseClass = lecturerCourseClassMapper.toEntity(requestDTO);
        
        Users lecturer = userRepository.findById(requestDTO.getLecturerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy giảng viên"));
        CourseSection courseSection = courseSectionRepository.findById(requestDTO.getCourseSectionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học phần"));
        
        lecturerCourseClass.setLecturer(lecturer);
        lecturerCourseClass.setCourseSection(courseSection);
        lecturerCourseClass.setActive(true);
        lecturerCourseClass.setCreatedAt(LocalDateTime.now());
        
        return lecturerCourseClassMapper.toResponseDTO(lecturerCourseClassRepository.save(lecturerCourseClass));
    }

    @Override
    public List<LecturerCourseClassResponseDTO> getAll() {
        return lecturerCourseClassRepository.findAll().stream()
                .map(lecturerCourseClassMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LecturerCourseClassResponseDTO getById(UUID id) {
        LecturerCourseClass lecturerCourseClass = lecturerCourseClassRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy phân công giảng viên"));
        return lecturerCourseClassMapper.toResponseDTO(lecturerCourseClass);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        LecturerCourseClass lecturerCourseClass = lecturerCourseClassRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy phân công giảng viên"));
        lecturerCourseClass.softDelete("system");
        lecturerCourseClassRepository.save(lecturerCourseClass);
    }
}
