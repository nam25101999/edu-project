package com.edu.university.modules.registration.service.impl;

import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.registration.dto.request.EquivalentCourseRequestDTO;
import com.edu.university.modules.registration.dto.response.EquivalentCourseResponseDTO;
import com.edu.university.modules.registration.entity.EquivalentCourse;
import com.edu.university.modules.registration.mapper.EquivalentCourseMapper;
import com.edu.university.modules.registration.repository.EquivalentCourseRepository;
import com.edu.university.modules.registration.service.EquivalentCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquivalentCourseServiceImpl implements EquivalentCourseService {

    private final EquivalentCourseRepository equivalentCourseRepository;
    private final CourseRepository courseRepository;
    private final EquivalentCourseMapper equivalentCourseMapper;

    @Override
    @Transactional
    public EquivalentCourseResponseDTO create(EquivalentCourseRequestDTO requestDTO) {
        EquivalentCourse equivalentCourse = equivalentCourseMapper.toEntity(requestDTO);
        Course original = courseRepository.findById(requestDTO.getOriginalCourseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học gốc"));
        Course substitute = courseRepository.findById(requestDTO.getEquivalentCourseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học thay thế"));
        
        equivalentCourse.setOriginalCourse(original);
        equivalentCourse.setEquivalentCourse(substitute);
        equivalentCourse.setActive(true);
        equivalentCourse.setCreatedAt(LocalDateTime.now());
        
        return equivalentCourseMapper.toResponseDTO(equivalentCourseRepository.save(equivalentCourse));
    }

    @Override
    public List<EquivalentCourseResponseDTO> getAll() {
        return equivalentCourseRepository.findAll().stream()
                .map(equivalentCourseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EquivalentCourseResponseDTO getById(UUID id) {
        EquivalentCourse equivalentCourse = equivalentCourseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin môn tương đương"));
        return equivalentCourseMapper.toResponseDTO(equivalentCourse);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        EquivalentCourse equivalentCourse = equivalentCourseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin môn tương đương"));
        equivalentCourse.softDelete("system");
        equivalentCourseRepository.save(equivalentCourse);
    }
}
