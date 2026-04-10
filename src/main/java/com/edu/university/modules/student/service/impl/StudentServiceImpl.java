package com.edu.university.modules.student.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.repository.MajorRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.registration.entity.CourseRegistration;
import com.edu.university.modules.registration.repository.CourseRegistrationRepository;
import com.edu.university.modules.schedule.dto.response.StudentScheduleResponseDTO;
import com.edu.university.modules.schedule.entity.Schedule;
import com.edu.university.modules.schedule.repository.ScheduleRepository;
import com.edu.university.modules.student.dto.request.StudentBulkDeleteRequestDTO;
import com.edu.university.modules.student.dto.request.StudentBulkStatusRequestDTO;
import com.edu.university.modules.student.dto.request.StudentRequestDTO;
import com.edu.university.modules.student.dto.request.StudentStatusChangeRequestDTO;
import com.edu.university.modules.student.dto.response.StudentImportResultDTO;
import com.edu.university.modules.student.dto.response.StudentResponseDTO;
import com.edu.university.modules.student.dto.response.StudentStatsResponseDTO;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.entity.StudentClassSection;
import com.edu.university.modules.student.mapper.StudentMapper;
import com.edu.university.modules.student.repository.StudentClassSectionRepository;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private static final List<String> IMPORT_HEADERS = List.of(
            "studentCode", "firstName", "lastName", "email", "phone",
            "departmentId", "majorId", "programId", "dateOfBirth", "gender", "address");

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final MajorRepository majorRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final StudentClassSectionRepository studentClassSectionRepository;

    @Override
    @Transactional
    public StudentResponseDTO createStudent(StudentRequestDTO requestDTO) {
        validateStudentCodeForCreate(requestDTO.getStudentCode());

        UUID finalUserId = requestDTO.getUserId();
        if (finalUserId == null) {
            finalUserId = createStudentUser(requestDTO).getId();
        }

        Student student = studentMapper.toEntity(requestDTO);
        Users user = userRepository.findById(finalUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Khong tim thay tai khoan nguoi dung"));

        syncUserData(user, requestDTO.getStudentCode(), requestDTO.getEmail(), true);
        student.setUser(user);
        student.setDepartment(resolveDepartment(requestDTO.getDepartmentId()));
        student.setMajor(resolveMajor(requestDTO.getMajorId()));
        student.setTrainingProgram(resolveTrainingProgram(requestDTO.getProgramId()));
        student.setActive(true);

        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getAllStudents(String search, Boolean isActive, Pageable pageable) {
        return studentRepository.searchStudents(search, isActive, pageable)
                .map(studentMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(UUID id) {
        Student student = findStudentOrThrow(id);
        return studentMapper.toResponseDTO(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentByCode(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Khong tim thay sinh vien"));
        return studentMapper.toResponseDTO(student);
    }

    @Override
    @Transactional
    public StudentResponseDTO updateStudent(UUID id, StudentRequestDTO requestDTO) {
        Student student = findStudentOrThrow(id);

        if (!student.getStudentCode().equals(requestDTO.getStudentCode())
                && studentRepository.existsByStudentCode(requestDTO.getStudentCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Ma sinh vien da ton tai");
        }

        studentMapper.updateEntityFromDTO(requestDTO, student);
        student.setDepartment(resolveDepartment(requestDTO.getDepartmentId()));
        student.setMajor(resolveMajor(requestDTO.getMajorId()));
        student.setTrainingProgram(resolveTrainingProgram(requestDTO.getProgramId()));

        if (student.getUser() != null) {
            syncUserData(student.getUser(), requestDTO.getStudentCode(), requestDTO.getEmail(), student.isActive());
        }

        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    @Override
    @Transactional
    public void deleteStudent(UUID id) {
        Student student = findStudentOrThrow(id);
        if (student.getUser() != null) {
            student.getUser().setIsActive(false);
            userRepository.save(student.getUser());
        }
        student.softDelete(resolveActor());
        studentRepository.save(student);
    }

    @Override
    @Transactional
    public void bulkDeleteStudents(StudentBulkDeleteRequestDTO requestDTO) {
        requestDTO.getStudentIds().forEach(this::deleteStudent);
    }

    @Override
    @Transactional
    public StudentResponseDTO changeStatus(UUID id, StudentStatusChangeRequestDTO requestDTO) {
        Student student = findStudentOrThrow(id);
        student.setActive(requestDTO.getIsActive());
        if (student.getUser() != null) {
            student.getUser().setIsActive(requestDTO.getIsActive());
            userRepository.save(student.getUser());
        }
        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    @Override
    @Transactional
    public List<StudentResponseDTO> bulkChangeStatus(StudentBulkStatusRequestDTO requestDTO) {
        StudentStatusChangeRequestDTO statusRequestDTO = new StudentStatusChangeRequestDTO();
        statusRequestDTO.setIsActive(requestDTO.getIsActive());
        return requestDTO.getStudentIds().stream()
                .map(id -> changeStatus(id, statusRequestDTO))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentStatsResponseDTO getStudentStats() {
        long totalStudents = studentRepository.count();
        long activeStudents = studentRepository.countByIsActiveTrue();
        long inactiveStudents = studentRepository.countByIsActiveFalse();

        return StudentStatsResponseDTO.builder()
                .totalStudents(totalStudents)
                .activeStudents(activeStudents)
                .inactiveStudents(inactiveStudents)
                .build();
    }

    @Override
    @Transactional
    public StudentImportResultDTO importStudents(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "File import khong duoc de trong");
        }

        List<String> errors = new ArrayList<>();
        int totalRows = 0;
        int createdCount = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "File CSV khong co du lieu");
            }

            List<String> headers = parseCsvLine(headerLine);
            validateImportHeaders(headers);
            Map<String, Integer> headerIndex = buildHeaderIndex(headers);

            String line;
            int rowNumber = 1;
            while ((line = reader.readLine()) != null) {
                rowNumber++;
                if (line.isBlank()) {
                    continue;
                }

                totalRows++;
                try {
                    List<String> values = parseCsvLine(line);
                    StudentRequestDTO requestDTO = buildImportRequest(values, headerIndex);
                    createStudent(requestDTO);
                    createdCount++;
                } catch (Exception ex) {
                    errors.add("Dong " + rowNumber + ": " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Khong the doc file import");
        }

        return StudentImportResultDTO.builder()
                .totalRows(totalRows)
                .createdCount(createdCount)
                .failedCount(totalRows - createdCount)
                .errors(errors)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportStudents(String search, Boolean isActive, Pageable pageable) {
        Page<Student> page = studentRepository.searchStudents(search, isActive, pageable);
        StringBuilder csv = new StringBuilder();
        csv.append(String.join(",", IMPORT_HEADERS)).append("\n");

        for (Student student : page.getContent()) {
            csv.append(csvValue(student.getStudentCode())).append(',')
                    .append(csvValue(student.getFirstName())).append(',')
                    .append(csvValue(student.getLastName())).append(',')
                    .append(csvValue(student.getEmail())).append(',')
                    .append(csvValue("")).append(',')
                    .append(csvValue(
                            student.getDepartment() != null ? student.getDepartment().getId().toString() : null))
                    .append(',')
                    .append(csvValue(student.getMajor() != null ? student.getMajor().getId().toString() : null))
                    .append(',')
                    .append(csvValue(
                            student.getTrainingProgram() != null ? student.getTrainingProgram().getId().toString()
                                    : null))
                    .append(',')
                    .append(csvValue(student.getDateOfBirth() != null ? student.getDateOfBirth().toString() : null))
                    .append(',')
                    .append(csvValue(student.getGender())).append(',')
                    .append(csvValue(student.getAddress()))
                    .append('\n');
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getStudentsByStudentClass(UUID studentClassId, String search, Boolean isActive,
            Pageable pageable) {
        List<Student> students = studentClassSectionRepository.findByStudentClassIdAndIsActiveTrue(studentClassId)
                .stream()
                .map(StudentClassSection::getStudent)
                .filter(student -> student != null)
                .distinct()
                .toList();

        return toStudentPage(students, search, isActive, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponseDTO> getStudentsByCourseSection(UUID courseSectionId, String search, Boolean isActive,
            Pageable pageable) {
        List<Student> students = courseRegistrationRepository.findByCourseSectionIdAndStatus(courseSectionId, 1)
                .stream()
                .map(CourseRegistration::getStudent)
                .filter(student -> student != null)
                .distinct()
                .toList();

        return toStudentPage(students, search, isActive, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getCurrentStudentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new RuntimeException("Nguoi dung chua dang nhap hoac phien lam viec het han");
        }

        Student student = studentRepository.findByUserId(userDetails.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND,
                        "Khong tim thay ho so sinh vien lien ket voi tai khoan nay"));

        return studentMapper.toResponseDTO(student);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentScheduleResponseDTO> getMySchedule(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new RuntimeException("Nguoi dung chua dang nhap hoac phien lam viec het han");
        }

        Student student = studentRepository.findByUserId(userDetails.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Khong tim thay ho so sinh vien"));

        List<CourseRegistration> registrations = courseRegistrationRepository.findByStudentId(student.getId());
        List<UUID> sectionIds = registrations.stream()
                .filter(reg -> reg.getStatus() != null && reg.getStatus() == 1)
                .map(reg -> reg.getCourseSection().getId())
                .toList();

        List<StudentScheduleResponseDTO> allSchedules = sectionIds.stream()
                .flatMap(id -> scheduleRepository.findAllByCourseSectionId(id).stream())
                .map(this::mapToScheduleResponseDTO)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allSchedules.size());

        if (start > allSchedules.size()) {
            return new PageImpl<>(List.of(), pageable, allSchedules.size());
        }

        return new PageImpl<>(allSchedules.subList(start, end), pageable, allSchedules.size());
    }

    private Users createStudentUser(StudentRequestDTO requestDTO) {
        if (userRepository.existsByUsername(requestDTO.getStudentCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS,
                    "Ten dang nhap (ma sinh vien) da ton tai trong he thong");
        }

        ensureEmailNotUsed(requestDTO.getEmail(), null);

        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND,
                        "Khong tim thay vai tro STUDENT trong he thong"));

        Users newUser = Users.builder()
                .username(requestDTO.getStudentCode())
                .password(passwordEncoder.encode("123456"))
                .email(requestDTO.getEmail())
                .roles(Set.of(studentRole))
                .isActive(true)
                .emailVerified(true)
                .build();

        return userRepository.save(newUser);
    }

    private void validateStudentCodeForCreate(String studentCode) {
        if (studentRepository.existsByStudentCode(studentCode)) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Ma sinh vien da ton tai");
        }
    }

    private Student findStudentOrThrow(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Khong tim thay sinh vien"));
    }

    private Department resolveDepartment(UUID departmentId) {
        if (departmentId == null) {
            return null;
        }

        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Khong tim thay khoa"));
    }

    private Major resolveMajor(UUID majorId) {
        if (majorId == null) {
            return null;
        }

        return majorRepository.findById(majorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Khong tim thay nganh"));
    }

    private TrainingProgram resolveTrainingProgram(UUID programId) {
        if (programId == null) {
            return null;
        }

        return trainingProgramRepository.findById(programId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Khong tim thay chuong trinh dao tao"));
    }

    private void syncUserData(Users user, String username, String email, boolean isActive) {
        ensureEmailNotUsed(email, user.getId());
        user.setUsername(username);
        user.setEmail(email);
        user.setIsActive(isActive);
        userRepository.save(user);
    }

    private void ensureEmailNotUsed(String email, UUID currentUserId) {
        if (email == null || email.isBlank()) {
            return;
        }

        userRepository.findByEmail(email)
                .filter(existing -> currentUserId == null || !existing.getId().equals(currentUserId))
                .ifPresent(existing -> {
                    throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Email da duoc su dung boi tai khoan khac");
                });
    }

    private String resolveActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getUsername();
        }
        return "system";
    }

    private void validateImportHeaders(List<String> headers) {
        List<String> normalizedHeaders = headers.stream()
                .map(String::trim)
                .toList();
        if (!normalizedHeaders.containsAll(IMPORT_HEADERS)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,
                    "Header CSV khong hop le. Can co cac cot: " + String.join(", ", IMPORT_HEADERS));
        }
    }

    private Map<String, Integer> buildHeaderIndex(List<String> headers) {
        Map<String, Integer> headerIndex = new LinkedHashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            headerIndex.put(headers.get(i).trim(), i);
        }
        return headerIndex;
    }

    private StudentRequestDTO buildImportRequest(List<String> values, Map<String, Integer> headerIndex) {
        StudentRequestDTO requestDTO = new StudentRequestDTO();
        requestDTO.setStudentCode(getCsvValue(values, headerIndex, "studentCode"));
        requestDTO.setFirstName(getCsvValue(values, headerIndex, "firstName"));
        requestDTO.setLastName(getCsvValue(values, headerIndex, "lastName"));
        requestDTO.setEmail(getCsvValue(values, headerIndex, "email"));
        requestDTO.setPhone(getCsvValue(values, headerIndex, "phone"));
        requestDTO.setAddress(getCsvValue(values, headerIndex, "address"));

        String departmentId = getCsvValue(values, headerIndex, "departmentId");
        String majorId = getCsvValue(values, headerIndex, "majorId");
        String programId = getCsvValue(values, headerIndex, "programId");
        String dateOfBirth = getCsvValue(values, headerIndex, "dateOfBirth");
        String gender = getCsvValue(values, headerIndex, "gender");

        if (departmentId != null && !departmentId.isBlank()) {
            requestDTO.setDepartmentId(UUID.fromString(departmentId));
        }
        if (majorId != null && !majorId.isBlank()) {
            requestDTO.setMajorId(UUID.fromString(majorId));
        }
        if (programId != null && !programId.isBlank()) {
            requestDTO.setProgramId(UUID.fromString(programId));
        }
        if (dateOfBirth != null && !dateOfBirth.isBlank()) {
            requestDTO.setDateOfBirth(LocalDate.parse(dateOfBirth));
        }
        if (gender != null && !gender.isBlank()) {
            requestDTO.setGender(Integer.parseInt(gender));
        }

        return requestDTO;
    }

    private String getCsvValue(List<String> values, Map<String, Integer> headerIndex, String key) {
        Integer index = headerIndex.get(key);
        if (index == null || index >= values.size()) {
            return null;
        }
        String value = values.get(index);
        return value == null ? null : value.trim();
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        values.add(current.toString());
        return values.stream().map(this::stripCsvQuotes).toList();
    }

    private String stripCsvQuotes(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            return trimmed.substring(1, trimmed.length() - 1).replace("\"\"", "\"");
        }
        return trimmed;
    }

    private String csvValue(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private StudentScheduleResponseDTO mapToScheduleResponseDTO(Schedule schedule) {
        CourseSection section = schedule.getCourseSection();
        return StudentScheduleResponseDTO.builder()
                .courseName(section.getCourse() != null ? section.getCourse().getName() : "N/A")
                .classCode(section.getClassCode())
                .roomName(schedule.getRoom() != null ? schedule.getRoom().getRoomName() : "N/A")
                .buildingName(schedule.getRoom() != null && schedule.getRoom().getBuilding() != null
                        ? schedule.getRoom().getBuilding().getBuildingName()
                        : "N/A")
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

    private Page<StudentResponseDTO> toStudentPage(List<Student> students, String search, Boolean isActive,
            Pageable pageable) {
        List<Student> filteredStudents = students.stream()
                .filter(student -> matchesSearch(student, search))
                .filter(student -> matchesStatus(student, isActive))
                .sorted(buildStudentComparator(pageable))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredStudents.size());

        if (start >= filteredStudents.size()) {
            return new PageImpl<>(List.of(), pageable, filteredStudents.size());
        }

        List<StudentResponseDTO> content = filteredStudents.subList(start, end).stream()
                .map(studentMapper::toResponseDTO)
                .toList();

        return new PageImpl<>(content, pageable, filteredStudents.size());
    }

    private boolean matchesSearch(Student student, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }

        String normalizedSearch = search.trim().toLowerCase();
        return containsIgnoreCase(student.getFullName(), normalizedSearch)
                || containsIgnoreCase(student.getFirstName(), normalizedSearch)
                || containsIgnoreCase(student.getLastName(), normalizedSearch)
                || containsIgnoreCase(student.getStudentCode(), normalizedSearch)
                || containsIgnoreCase(student.getEmail(), normalizedSearch);
    }

    private boolean matchesStatus(Student student, Boolean isActive) {
        return isActive == null || student.isActive() == isActive;
    }

    private boolean containsIgnoreCase(String value, String search) {
        return value != null && value.toLowerCase().contains(search);
    }

    private Comparator<Student> buildStudentComparator(Pageable pageable) {
        Comparator<Student> comparator = Comparator.comparing(Student::getCreatedAt,
                Comparator.nullsLast(LocalDateTime::compareTo));

        if (pageable.getSort().isSorted()) {
            var order = pageable.getSort().iterator().next();
            String property = order.getProperty();
            if ("fullName".equals(property)) {
                comparator = Comparator.comparing(Student::getFullName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            } else if ("studentCode".equals(property)) {
                comparator = Comparator.comparing(Student::getStudentCode,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            }

            if (order.isDescending()) {
                comparator = comparator.reversed();
            }
            return comparator;
        }

        return comparator.reversed();
    }
}
