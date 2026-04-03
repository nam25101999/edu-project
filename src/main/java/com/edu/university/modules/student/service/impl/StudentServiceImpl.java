package com.edu.university.modules.student.service.impl;

import com.edu.university.modules.student.dto.request.StudentRequestDTO;
import com.edu.university.modules.student.dto.request.StudentStatusChangeRequestDTO;
import com.edu.university.modules.student.dto.response.StudentResponseDTO;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.mapper.StudentMapper;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.student.service.StudentService;
import com.edu.university.modules.registration.repository.CourseRegistrationRepository;
import com.edu.university.modules.schedule.repository.ScheduleRepository;
import com.edu.university.modules.schedule.dto.response.StudentScheduleResponseDTO;
import com.edu.university.modules.registration.entity.CourseRegistration;
import com.edu.university.modules.schedule.entity.Schedule;
import com.edu.university.modules.academic.entity.CourseSection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public StudentResponseDTO createStudent(StudentRequestDTO requestDTO) {
        if (studentRepository.existsByStudentCode(requestDTO.getStudentCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã sinh viên đã tồn tại");
        }
        Student student = studentMapper.toEntity(requestDTO);
        student.setActive(true);
        student.setCreatedAt(LocalDateTime.now());
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toResponseDTO(savedStudent);
    }

    @Override
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(studentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponseDTO getStudentById(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy sinh viên"));
        return studentMapper.toResponseDTO(student);
    }

    @Override
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
        student.setUpdatedAt(LocalDateTime.now());

        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    @Override
    @Transactional
    public void deleteStudent(UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy sinh viên"));
        // Xóa mềm
        student.setActive(false);
        student.setDeletedAt(LocalDateTime.now());
        studentRepository.save(student);
    }

    @Override
    @Transactional
    public StudentResponseDTO changeStatus(UUID id, StudentStatusChangeRequestDTO requestDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy sinh viên"));

        // Lưu ý: Tuỳ thuộc vào Entity và DTO, bạn có thể gọi requestDTO.getIsActive() hoặc requestDTO.isActive()
        student.setActive(requestDTO.getIsActive());

        student.setUpdatedAt(LocalDateTime.now());
        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    @Override
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
    public List<StudentScheduleResponseDTO> getMySchedule() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new RuntimeException("Người dùng chưa đăng nhập hoặc phiên làm việc hết hạn");
        }

        Student student = studentRepository.findByUserId(userDetails.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy hồ sơ sinh viên"));

        // Lấy tất cả đăng ký môn của sinh viên này
        List<CourseRegistration> registrations = courseRegistrationRepository.findByStudentId(student.getId());
        
        // Trích xuất các ID của lớp HP (CourseSection)
        List<UUID> sectionIds = registrations.stream()
                .filter(reg -> reg.getStatus() != null && reg.getStatus() == 1) // 1 thường là đăng ký thành công
                .map(reg -> reg.getCourseSection().getId())
                .toList();

        // Với mỗi lớp HP, tìm lịch học tương ứng
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