package com.edu.university.modules.academic.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.dto.request.CourseSectionRequestDTO;
import com.edu.university.modules.academic.dto.response.CourseSectionResponseDTO;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.mapper.CourseSectionMapper;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.academic.service.CourseSectionService;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseSectionServiceImpl implements CourseSectionService {

    private final CourseSectionRepository courseSectionRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final CourseSectionMapper courseSectionMapper;

    @Override
    @Transactional
    public CourseSectionResponseDTO create(CourseSectionRequestDTO requestDTO) {
        if (courseSectionRepository.existsByClassCode(requestDTO.getClassCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã lớp học phần đã tồn tại");
        }
        CourseSection courseSection = courseSectionMapper.toEntity(requestDTO);
        
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy môn học"));
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy học kỳ"));
        
        courseSection.setCourse(course);
        courseSection.setSemester(semester);
        courseSection.setActive(true);
        courseSection.setCreatedAt(LocalDateTime.now());
        
        return courseSectionMapper.toResponseDTO(courseSectionRepository.save(courseSection));
    }

    @Override
    public List<CourseSectionResponseDTO> getAll() {
        return courseSectionRepository.findAll().stream()
                .map(courseSectionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseSectionResponseDTO getById(UUID id) {
        CourseSection courseSection = courseSectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học phần"));
        return courseSectionMapper.toResponseDTO(courseSection);
    }

    @Override
    @Transactional
    public CourseSectionResponseDTO update(UUID id, CourseSectionRequestDTO requestDTO) {
        CourseSection courseSection = courseSectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học phần"));
        
        courseSectionMapper.updateEntityFromDTO(requestDTO, courseSection);
        
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy môn học"));
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy học kỳ"));
        
        courseSection.setCourse(course);
        courseSection.setSemester(semester);
        courseSection.setUpdatedAt(LocalDateTime.now());
        
        return courseSectionMapper.toResponseDTO(courseSectionRepository.save(courseSection));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        CourseSection courseSection = courseSectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học phần"));
        courseSection.softDelete("system");
        courseSectionRepository.save(courseSection);
    }
}
