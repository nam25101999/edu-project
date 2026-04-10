package com.edu.university.modules.registration.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.curriculum.entity.CoursePrerequisite;
import com.edu.university.modules.curriculum.repository.CoursePrerequisiteRepository;
import com.edu.university.modules.grading.repository.StudentSummaryRepository;
import com.edu.university.modules.registration.dto.request.CourseRegistrationRequestDTO;
import com.edu.university.modules.registration.dto.request.EligibilityCheckRequest;
import com.edu.university.modules.registration.dto.response.CourseRegistrationResponseDTO;
import com.edu.university.modules.registration.dto.response.EligibilityCheckResponse;
import com.edu.university.modules.registration.entity.CourseRegistration;
import com.edu.university.modules.registration.entity.RegistrationPeriod;
import com.edu.university.modules.registration.mapper.CourseRegistrationMapper;
import com.edu.university.modules.registration.repository.CourseRegistrationRepository;
import com.edu.university.modules.registration.repository.RegistrationPeriodRepository;
import com.edu.university.modules.registration.service.CourseRegistrationService;
import com.edu.university.modules.schedule.entity.Schedule;
import com.edu.university.modules.schedule.repository.ScheduleRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseRegistrationServiceImpl implements CourseRegistrationService {

    private final CourseRegistrationRepository courseRegistrationRepository;
    private final StudentRepository studentRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final RegistrationPeriodRepository registrationPeriodRepository;
    private final CourseRegistrationMapper courseRegistrationMapper;
    private final CoursePrerequisiteRepository coursePrerequisiteRepository;
    private final StudentSummaryRepository studentSummaryRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public CourseRegistrationResponseDTO create(CourseRegistrationRequestDTO requestDTO) {
        if (courseRegistrationRepository.existsByStudentIdAndCourseSectionId(requestDTO.getStudentId(), requestDTO.getCourseSectionId())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Sinh viên đã đăng ký lớp học phần này");
        }
        
        CourseRegistration registration = courseRegistrationMapper.toEntity(requestDTO);
        
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));
        CourseSection courseSection = courseSectionRepository.findById(requestDTO.getCourseSectionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_SECTION_NOT_FOUND));
        RegistrationPeriod period = registrationPeriodRepository.findById(requestDTO.getRegistrationPeriodId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REGISTRATION_PERIOD_NOT_FOUND));
        
        registration.setStudent(student);
        registration.setCourseSection(courseSection);
        registration.setRegistrationPeriod(period);
        registration.setActive(true);
        if (registration.getRegisteredAt() == null) {
            registration.setRegisteredAt(LocalDateTime.now());
        }
        
        return courseRegistrationMapper.toResponseDTO(courseRegistrationRepository.save(registration));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseRegistrationResponseDTO> getAll(Pageable pageable) {
        return courseRegistrationRepository.findAll(pageable)
                .map(courseRegistrationMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseRegistrationResponseDTO getById(UUID id) {
        CourseRegistration registration = courseRegistrationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy thông tin đăng ký"));
        return courseRegistrationMapper.toResponseDTO(registration);
    }

    @Override
    @Transactional
    public CourseRegistrationResponseDTO update(UUID id, CourseRegistrationRequestDTO requestDTO) {
        CourseRegistration registration = courseRegistrationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy thông tin đăng ký"));
        
        courseRegistrationMapper.updateEntityFromDTO(requestDTO, registration);
        
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));
        CourseSection courseSection = courseSectionRepository.findById(requestDTO.getCourseSectionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_SECTION_NOT_FOUND));
        RegistrationPeriod period = registrationPeriodRepository.findById(requestDTO.getRegistrationPeriodId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REGISTRATION_PERIOD_NOT_FOUND));
        
        registration.setStudent(student);
        registration.setCourseSection(courseSection);
        registration.setRegistrationPeriod(period);
        
        return courseRegistrationMapper.toResponseDTO(courseRegistrationRepository.save(registration));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        CourseRegistration registration = courseRegistrationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy thông tin đăng ký"));
        registration.softDelete("system");
        courseRegistrationRepository.save(registration);
    }

    @Override
    @Transactional(readOnly = true)
    public EligibilityCheckResponse checkEligibility(EligibilityCheckRequest request) {
        RegistrationPeriod period = registrationPeriodRepository.findById(request.getRegistrationPeriodId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REGISTRATION_PERIOD_NOT_FOUND));

        List<CourseRegistration> currentRegistrations = courseRegistrationRepository.findByRegistrationPeriodId(request.getRegistrationPeriodId())
                .stream()
                .filter(reg -> reg.getStudent().getId().equals(request.getStudentId()))
                .collect(Collectors.toList());

        // Optimization: Fetch all summaries for this student once
        List<com.edu.university.modules.grading.entity.StudentSummary> studentSummaries = 
                studentSummaryRepository.findByCourseRegistrationStudentId(request.getStudentId());

        List<EligibilityCheckResponse.Violation> violations = new ArrayList<>();
        int totalCredits = currentRegistrations.stream()
                .mapToInt(reg -> reg.getCourseSection().getCourse().getCredits().intValue())
                .sum();

        for (UUID sectionId : request.getCourseSectionIds()) {
            CourseSection section = courseSectionRepository.findById(sectionId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_SECTION_NOT_FOUND));

            if (currentRegistrations.stream().anyMatch(reg -> reg.getCourseSection().getId().equals(sectionId))) {
                continue;
            }

            // Optimization: Use targeted query for prerequisites
            List<CoursePrerequisite> prerequisites = coursePrerequisiteRepository.findByCourseId(section.getCourse().getId());

            for (CoursePrerequisite prereq : prerequisites) {
                boolean hasPassed = studentSummaries.stream()
                        .filter(s -> s.getCourseRegistration().getCourseSection().getCourse().getId().equals(prereq.getPrerequisiteCourse().getId()))
                        .anyMatch(s -> "PASS".equalsIgnoreCase(s.getResult()) || (s.getGpaValue() != null && s.getGpaValue().doubleValue() >= 2.0));

                if (!hasPassed) {
                    violations.add(EligibilityCheckResponse.Violation.builder()
                            .courseSectionId(sectionId)
                            .reason("PREREQUISITE_NOT_MET")
                            .message("Chưa hoàn thành môn tiên quyết: " + prereq.getPrerequisiteCourse().getName())
                            .build());
                }
            }

            List<Schedule> newSchedules = scheduleRepository.findAllByCourseSectionId(sectionId);
            for (CourseRegistration existingReg : currentRegistrations) {
                List<Schedule> existingSchedules = scheduleRepository.findAllByCourseSectionId(existingReg.getCourseSection().getId());
                for (Schedule ns : newSchedules) {
                    for (Schedule es : existingSchedules) {
                        if (ns.getDayOfWeek().equals(es.getDayOfWeek())) {
                            if (Math.max(ns.getStartPeriod(), es.getStartPeriod()) <= Math.min(ns.getEndPeriod(), es.getEndPeriod())) {
                                violations.add(EligibilityCheckResponse.Violation.builder()
                                        .courseSectionId(sectionId)
                                        .reason("SCHEDULE_CONFLICT")
                                        .message("Trùng lịch học với môn: " + existingReg.getCourseSection().getCourse().getName())
                                        .conflictingWithId(existingReg.getCourseSection().getId())
                                        .build());
                            }
                        }
                    }
                }
            }
            totalCredits += section.getCourse().getCredits().intValue();
        }

        if (totalCredits > period.getMaxCredits()) {
            violations.add(EligibilityCheckResponse.Violation.builder()
                    .reason("MAX_CREDITS_EXCEEDED")
                    .message("Vượt quá số tín chỉ tối đa (" + period.getMaxCredits() + ")")
                    .build());
        } else if (totalCredits < period.getMinCredits()) {
            violations.add(EligibilityCheckResponse.Violation.builder()
                    .reason("MIN_CREDITS_BELOW_MIN")
                    .message("Chưa đạt số tín chỉ tối thiểu (" + period.getMinCredits() + ")")
                    .build());
        }

        return EligibilityCheckResponse.builder()
                .isEligible(violations.isEmpty())
                .totalCredits(totalCredits)
                .violations(violations)
                .build();
    }
}
