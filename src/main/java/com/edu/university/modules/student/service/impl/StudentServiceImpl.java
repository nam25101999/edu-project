package com.edu.university.modules.student.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.registration.entity.CourseRegistration;
import com.edu.university.modules.registration.repository.CourseRegistrationRepository;
import com.edu.university.modules.schedule.dto.response.StudentScheduleResponseDTO;
import com.edu.university.modules.schedule.entity.Schedule;
import com.edu.university.modules.schedule.repository.ScheduleRepository;
import com.edu.university.modules.student.dto.request.StudentRequestDTO;
import com.edu.university.modules.student.dto.request.StudentStatusChangeRequestDTO;
import com.edu.university.modules.student.dto.response.StudentResponseDTO;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.mapper.StudentMapper;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public StudentResponseDTO createStudent(StudentRequestDTO requestDTO) {
        if (studentRepository.existsByStudentCode(requestDTO.getStudentCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã sinh viên đã tồn tại");
        }

        UUID finalUserId = requestDTO.getUserId();
        if (finalUserId == null) {
            if (userRepository.existsByUsername(requestDTO.getStudentCode())) {
                 throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Tên đăng nhập (mã SV) đã tồn tại trong hệ thống tài khoản");
            }

            Role studentRole = roleRepository.findByName("STUDENT")
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy vai trò STUDENT trong hệ thống"));

            Users newUser = Users.builder()
                    .username(requestDTO.getStudentCode())
                    .password(passwordEncoder.encode("123456"))
                    .email(requestDTO.getEmail())
                    .roles(Set.of(studentRole))
                    .isActive(true)
                    .emailVerified(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            Users savedUser = userRepository.save(newUser);
            finalUserId = savedUser.getId();
        }

        Student student = studentMapper.toEntity(requestDTO);
        Users user = userRepository.findById(finalUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy tài khoản người dùng"));
        student.setUser(user);

        if (requestDTO.getGender() != null) {
            student.setGender(String.valueOf(requestDTO.getGender()));
        }

        student.setActive(true);
        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable)
                .map(studentMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy sinh viên"));
        return studentMapper.toResponseDTO(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentByCode(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy sinh viên"));
        return studentMapper.toResponseDTO(student);
    }

    @Override
    @Transactional
    public StudentResponseDTO updateStudent(UUID id, StudentRequestDTO requestDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy sinh viên"));
        studentMapper.updateEntityFromDTO(requestDTO, student);
        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    @Override
    @Transactional
    public void deleteStudent(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy sinh viên"));
        student.softDelete("system");
        studentRepository.save(student);
    }

    @Override
    @Transactional
    public StudentResponseDTO changeStatus(UUID id, StudentStatusChangeRequestDTO requestDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy sinh viên"));
        student.setActive(requestDTO.getIsActive());
        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getCurrentStudentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new RuntimeException("Người dùng chưa đăng nhập hoặc phiên làm việc hết hạn");
        }

        Student student = studentRepository.findByUserId(userDetails.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy hồ sơ sinh viên liên kết với tài khoản này"));

        return studentMapper.toResponseDTO(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentScheduleResponseDTO> getMySchedule() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new RuntimeException("Người dùng chưa đăng nhập hoặc phiên làm việc hết hạn");
        }

        Student student = studentRepository.findByUserId(userDetails.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy hồ sơ sinh viên"));

        List<CourseRegistration> registrations = courseRegistrationRepository.findByStudentId(student.getId());
        List<UUID> sectionIds = registrations.stream()
                .filter(reg -> reg.getStatus() != null && reg.getStatus() == 1)
                .map(reg -> reg.getCourseSection().getId())
                .toList();

        return sectionIds.stream()
                .flatMap(id -> scheduleRepository.findByCourseSectionId(id).stream())
                .map(this::mapToScheduleResponseDTO)
                .toList();
    }

    private StudentScheduleResponseDTO mapToScheduleResponseDTO(Schedule schedule) {
        CourseSection section = schedule.getCourseSection();
        return StudentScheduleResponseDTO.builder()
                .courseName(section.getCourse() != null ? section.getCourse().getName() : "N/A")
                .classCode(section.getClassCode())
                .roomName(schedule.getRoom() != null ? schedule.getRoom().getRoomName() : "N/A")
                .buildingName(schedule.getRoom() != null && schedule.getRoom().getBuilding() != null 
                        ? schedule.getRoom().getBuilding().getBuildingName() : "N/A")
                .lecturerName(schedule.getLecturer() != null ? schedule.getLecturer().getUsername() : "N/A")
                .dayOfWeek(schedule.getDayOfWeek())
                .date(schedule.getDate())
                .shift(schedule.getShift())
                .startPeriod(schedule.getStartPeriod())
                .endPeriod(schedule.getEndPeriod())
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .build();
    }
}