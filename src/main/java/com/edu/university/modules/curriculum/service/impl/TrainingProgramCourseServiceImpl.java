package com.edu.university.modules.curriculum.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.curriculum.dto.request.TrainingProgramCourseRequestDTO;
import com.edu.university.modules.curriculum.dto.response.TrainingProgramCourseResponseDTO;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.entity.TrainingProgramCourse;
import com.edu.university.modules.curriculum.mapper.TrainingProgramCourseMapper;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramCourseRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.curriculum.service.TrainingProgramCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingProgramCourseServiceImpl implements TrainingProgramCourseService {

    private final TrainingProgramCourseRepository trainingProgramCourseRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final CourseRepository courseRepository;
    private final TrainingProgramCourseMapper trainingProgramCourseMapper;

    @Override
    @Transactional
    public TrainingProgramCourseResponseDTO create(TrainingProgramCourseRequestDTO requestDTO) {
        TrainingProgramCourse trainingProgramCourse = trainingProgramCourseMapper.toEntity(requestDTO);
        TrainingProgram trainingProgram = trainingProgramRepository.findById(requestDTO.getTrainingProgramId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy chương trình đào tạo"));
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy môn học"));
        
        trainingProgramCourse.setTrainingProgram(trainingProgram);
        trainingProgramCourse.setCourse(course);
        if (requestDTO.getPrerequisiteCourseId() != null) {
            Course prerequisiteCourse = courseRepository.findById(requestDTO.getPrerequisiteCourseId())
                    .orElse(null);
            trainingProgramCourse.setPrerequisiteCourse(prerequisiteCourse);
        }
        
        trainingProgramCourse.setActive(true);
        trainingProgramCourse.setCreatedAt(LocalDateTime.now());
        return trainingProgramCourseMapper.toResponseDTO(trainingProgramCourseRepository.save(trainingProgramCourse));
    }

    @Override
    public List<TrainingProgramCourseResponseDTO> getAll() {
        return trainingProgramCourseRepository.findAll().stream()
                .map(trainingProgramCourseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TrainingProgramCourseResponseDTO getById(UUID id) {
        TrainingProgramCourse trainingProgramCourse = trainingProgramCourseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy môn học trong chương trình đào tạo"));
        return trainingProgramCourseMapper.toResponseDTO(trainingProgramCourse);
    }

    @Override
    @Transactional
    public TrainingProgramCourseResponseDTO update(UUID id, TrainingProgramCourseRequestDTO requestDTO) {
        TrainingProgramCourse trainingProgramCourse = trainingProgramCourseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy môn học trong chương trình đào tạo"));
        trainingProgramCourseMapper.updateEntityFromDTO(requestDTO, trainingProgramCourse);
        
        TrainingProgram trainingProgram = trainingProgramRepository.findById(requestDTO.getTrainingProgramId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy chương trình đào tạo"));
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy môn học"));
        
        trainingProgramCourse.setTrainingProgram(trainingProgram);
        trainingProgramCourse.setCourse(course);
        if (requestDTO.getPrerequisiteCourseId() != null) {
            Course prerequisiteCourse = courseRepository.findById(requestDTO.getPrerequisiteCourseId())
                    .orElse(null);
            trainingProgramCourse.setPrerequisiteCourse(prerequisiteCourse);
        } else {
            trainingProgramCourse.setPrerequisiteCourse(null);
        }
        
        trainingProgramCourse.setUpdatedAt(LocalDateTime.now());
        return trainingProgramCourseMapper.toResponseDTO(trainingProgramCourseRepository.save(trainingProgramCourse));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        TrainingProgramCourse trainingProgramCourse = trainingProgramCourseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy môn học trong chương trình đào tạo"));
        trainingProgramCourse.softDelete("system");
        trainingProgramCourseRepository.save(trainingProgramCourse);
    }
}
