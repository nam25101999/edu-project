package com.edu.university.modules.registration.service.impl;

import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.registration.dto.request.CourseRegistrationRequestDTO;
import com.edu.university.modules.registration.dto.response.CourseRegistrationResponseDTO;
import com.edu.university.modules.registration.entity.CourseRegistration;
import com.edu.university.modules.registration.entity.RegistrationPeriod;
import com.edu.university.modules.registration.mapper.CourseRegistrationMapper;
import com.edu.university.modules.registration.repository.CourseRegistrationRepository;
import com.edu.university.modules.registration.repository.RegistrationPeriodRepository;
import com.edu.university.modules.registration.service.CourseRegistrationService;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Override
    @Transactional
    public CourseRegistrationResponseDTO create(CourseRegistrationRequestDTO requestDTO) {
        if (courseRegistrationRepository.existsByStudentIdAndCourseSectionId(requestDTO.getStudentId(), requestDTO.getCourseSectionId())) {
            throw new RuntimeException("Sinh viên đã đăng ký lớp học phần này");
        }
        
        CourseRegistration registration = courseRegistrationMapper.toEntity(requestDTO);
        
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));
        CourseSection courseSection = courseSectionRepository.findById(requestDTO.getCourseSectionId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần"));
        RegistrationPeriod period = registrationPeriodRepository.findById(requestDTO.getRegistrationPeriodId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt đăng ký"));
        
        registration.setStudent(student);
        registration.setCourseSection(courseSection);
        registration.setRegistrationPeriod(period);
        registration.setActive(true);
        registration.setCreatedAt(LocalDateTime.now());
        if (registration.getRegisteredAt() == null) {
            registration.setRegisteredAt(LocalDateTime.now());
        }
        
        return courseRegistrationMapper.toResponseDTO(courseRegistrationRepository.save(registration));
    }

    @Override
    public List<CourseRegistrationResponseDTO> getAll() {
        return courseRegistrationRepository.findAll().stream()
                .map(courseRegistrationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseRegistrationResponseDTO getById(UUID id) {
        CourseRegistration registration = courseRegistrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đăng ký"));
        return courseRegistrationMapper.toResponseDTO(registration);
    }

    @Override
    @Transactional
    public CourseRegistrationResponseDTO update(UUID id, CourseRegistrationRequestDTO requestDTO) {
        CourseRegistration registration = courseRegistrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đăng ký"));
        
        courseRegistrationMapper.updateEntityFromDTO(requestDTO, registration);
        
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));
        CourseSection courseSection = courseSectionRepository.findById(requestDTO.getCourseSectionId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần"));
        RegistrationPeriod period = registrationPeriodRepository.findById(requestDTO.getRegistrationPeriodId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt đăng ký"));
        
        registration.setStudent(student);
        registration.setCourseSection(courseSection);
        registration.setRegistrationPeriod(period);
        registration.setUpdatedAt(LocalDateTime.now());
        
        return courseRegistrationMapper.toResponseDTO(courseRegistrationRepository.save(registration));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        CourseRegistration registration = courseRegistrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đăng ký"));
        registration.softDelete("system");
        courseRegistrationRepository.save(registration);
    }
}
