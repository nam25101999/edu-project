package com.edu.university.modules.curriculum.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.curriculum.dto.request.TrainingProgramCourseRequestDTO;
import com.edu.university.modules.curriculum.dto.response.TrainingProgramCourseResponseDTO;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.entity.TrainingProgramCourse;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramCourseRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.curriculum.service.TrainingProgramCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainingProgramCourseServiceImpl implements TrainingProgramCourseService {

    private final TrainingProgramCourseRepository trainingProgramCourseRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public TrainingProgramCourseResponseDTO create(TrainingProgramCourseRequestDTO requestDTO) {
        TrainingProgram trainingProgram = trainingProgramRepository.findById(requestDTO.getTrainingProgramId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TRAINING_PROGRAM_NOT_FOUND));
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        
        Course prerequisiteCourse = null;
        if (requestDTO.getPrerequisiteCourseId() != null) {
            prerequisiteCourse = courseRepository.findById(requestDTO.getPrerequisiteCourseId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Không tìm thấy môn tiên quyết"));
        }

        TrainingProgramCourse tpc = new TrainingProgramCourse();
        tpc.setTrainingProgram(trainingProgram);
        tpc.setCourse(course);
        tpc.setCourseCode(course.getCourseCode());
        tpc.setCourseName(course.getName());
        tpc.setCredits(course.getCredits());
        tpc.setSemesterId(requestDTO.getSemesterId());
        tpc.setSemesterCode(requestDTO.getSemesterCode());
        tpc.setAcademicYear(requestDTO.getAcademicYear());
        tpc.setPrerequisiteCourse(prerequisiteCourse);
        tpc.setRequired(requestDTO.getIsRequired() != null ? requestDTO.getIsRequired() : true);
        tpc.setActive(true);
        
        return mapToResponseDTO(trainingProgramCourseRepository.save(tpc));
    }

    private TrainingProgramCourseResponseDTO mapToResponseDTO(TrainingProgramCourse entity) {
        return TrainingProgramCourseResponseDTO.builder()
                .id(entity.getId())
                .trainingProgramId(entity.getTrainingProgram() != null ? entity.getTrainingProgram().getId() : null)
                .programName(entity.getTrainingProgram() != null ? entity.getTrainingProgram().getProgramName() : null)
                .courseId(entity.getCourse() != null ? entity.getCourse().getId() : null)
                .courseCode(entity.getCourse() != null ? entity.getCourse().getCourseCode() : entity.getCourseCode())
                .courseName(entity.getCourse() != null ? entity.getCourse().getName() : entity.getCourseName())
                .credits(entity.getCourse() != null ? entity.getCourse().getCredits() : entity.getCredits())
                .isRequired(entity.isRequired())
                .isActive(entity.isActive())
                .prerequisiteCourseId(entity.getPrerequisiteCourse() != null ? entity.getPrerequisiteCourse().getId() : null)
                .prerequisiteCourseName(entity.getPrerequisiteCourse() != null ? entity.getPrerequisiteCourse().getName() : null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingProgramCourseResponseDTO> getAll(UUID trainingProgramId, Pageable pageable) {
        return trainingProgramCourseRepository.findPageWithRelations(trainingProgramId, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingProgramCourseResponseDTO getById(UUID id) {
        TrainingProgramCourse tpc = trainingProgramCourseRepository.findDetailById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRAINING_PROGRAM_COURSE_NOT_FOUND));
        return mapToResponseDTO(tpc);
    }

    @Override
    @Transactional
    public TrainingProgramCourseResponseDTO update(UUID id, TrainingProgramCourseRequestDTO requestDTO) {
        TrainingProgramCourse tpc = trainingProgramCourseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRAINING_PROGRAM_COURSE_NOT_FOUND));
        
        TrainingProgram trainingProgram = trainingProgramRepository.findById(requestDTO.getTrainingProgramId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TRAINING_PROGRAM_NOT_FOUND));
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        
        if (requestDTO.getIsRequired() != null) {
            tpc.setRequired(requestDTO.getIsRequired());
        }
        
        tpc.setTrainingProgram(trainingProgram);
        tpc.setCourse(course);
        tpc.setCourseCode(course.getCourseCode());
        tpc.setCourseName(course.getName());
        tpc.setCredits(course.getCredits());
        tpc.setSemesterId(requestDTO.getSemesterId());
        tpc.setSemesterCode(requestDTO.getSemesterCode());
        tpc.setAcademicYear(requestDTO.getAcademicYear());

        if (requestDTO.getPrerequisiteCourseId() != null) {
            Course prerequisiteCourse = courseRepository.findById(requestDTO.getPrerequisiteCourseId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Không tìm thấy môn tiên quyết"));
            tpc.setPrerequisiteCourse(prerequisiteCourse);
        } else {
            tpc.setPrerequisiteCourse(null);
        }
        
        return mapToResponseDTO(trainingProgramCourseRepository.save(tpc));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        TrainingProgramCourse tpc = trainingProgramCourseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRAINING_PROGRAM_COURSE_NOT_FOUND));
        tpc.softDelete("system");
        trainingProgramCourseRepository.save(tpc);
    }
}
