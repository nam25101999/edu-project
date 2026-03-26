package com.edu.university.modules.student.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.report.annotation.LogAction;
import com.edu.university.modules.student.dto.StudentDtos.StudentRequest;
import com.edu.university.modules.student.dto.StudentDtos.StudentResponse;
import com.edu.university.modules.student.entity.Major;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.entity.StudentClass;
import com.edu.university.modules.student.repository.MajorRepository;
import com.edu.university.modules.student.repository.StudentClassRepository;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service quản lý sinh viên.
 */
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepo;
    private final MajorRepository majorRepo;
    private final StudentClassRepository studentClassRepo;

    @Transactional(readOnly = true)
    public Page<StudentResponse> searchAndFilterStudents(String keyword, UUID majorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("studentCode").ascending());
        Page<Student> studentPage = studentRepo.searchAndFilterStudents(keyword, majorId, pageable);
        return studentPage.map(this::convertToResponse);
    }

    /**
     * Lấy danh sách sinh viên theo ID ngành học.
     *
     * @param majorId ID của ngành
     * @param page    số trang
     * @param size    kích thước trang
     * @return Trang danh sách sinh viên
     */
    @Transactional(readOnly = true)
    public Page<StudentResponse> getStudentsByMajor(UUID majorId, int page, int size) {
        // Kiểm tra sự tồn tại của ngành
        if (!majorRepo.existsById(majorId)) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy ngành học với ID: " + majorId);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("studentCode").ascending());
        return studentRepo.findByMajorId(majorId, pageable).map(this::convertToResponse);
    }
    @Transactional(readOnly = true)
    public Page<StudentResponse> getStudentsByFaculty(UUID facultyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("studentCode").ascending());
        // Truy vấn thông qua quan hệ Student -> Major -> Faculty
        return studentRepo.findByMajorFacultyId(facultyId, pageable).map(this::convertToResponse);
    }
    @Transactional(readOnly = true)
    public long countStudentsByMajor(UUID majorId) {
        return studentRepo.countByMajorId(majorId);
    }

    @Transactional(readOnly = true)
    public long countStudentsByFaculty(UUID facultyId) {
        return studentRepo.countByMajorFacultyId(facultyId);
    }

    @LogAction(action = "CREATE_STUDENT", entityName = "STUDENT")
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        if (studentRepo.existsByStudentCode(request.studentCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã sinh viên đã tồn tại");
        }

        Major major = majorRepo.findById(request.majorId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy ngành học"));

        StudentClass studentClass = studentClassRepo.findById(request.studentClassId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy lớp sinh hoạt"));

        Student student = Student.builder()
                .studentCode(request.studentCode())
                .fullName(request.fullName())
                .major(major)
                .studentClass(studentClass)
                .enrollmentYear(request.enrollmentYear())
                .avatarUrl(request.avatarUrl())
                .build();

        return convertToResponse(studentRepo.save(student));
    }

    @LogAction(action = "UPDATE_STUDENT", entityName = "STUDENT")
    @Transactional
    public StudentResponse updateStudent(UUID studentId, StudentRequest request) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy sinh viên"));

        if (!student.getStudentCode().equals(request.studentCode()) &&
                studentRepo.existsByStudentCode(request.studentCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã sinh viên đã tồn tại ở hồ sơ khác");
        }

        Major major = majorRepo.findById(request.majorId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy ngành học"));

        StudentClass studentClass = studentClassRepo.findById(request.studentClassId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy lớp sinh hoạt"));

        student.setStudentCode(request.studentCode());
        student.setFullName(request.fullName());
        student.setMajor(major);
        student.setStudentClass(studentClass);
        student.setEnrollmentYear(request.enrollmentYear());
        student.setAvatarUrl(request.avatarUrl());

        if (request.academicStatus() != null) {
            student.setAcademicStatus(request.academicStatus());
        }

        return convertToResponse(studentRepo.save(student));
    }

    @Transactional(readOnly = true)
    public StudentResponse getById(UUID id) {
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy sinh viên"));
        return convertToResponse(student);
    }

    @LogAction(action = "DELETE_STUDENT", entityName = "STUDENT")
    @Transactional
    public void deleteStudent(UUID studentId) {
        if (!studentRepo.existsById(studentId)) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy sinh viên để xóa");
        }
        studentRepo.deleteById(studentId);
    }

    private StudentResponse convertToResponse(Student student) {
        if (student == null) return null;

        Major major = student.getMajor();
        StudentClass studentClass = student.getStudentClass();

        return new StudentResponse(
                student.getId(),
                student.getStudentCode(),
                student.getFullName(),
                major != null ? major.getId() : null,
                major != null ? major.getName() : "Chưa có ngành",
                (major != null && major.getFaculty() != null) ? major.getFaculty().getName() : "Chưa có khoa",
                studentClass != null ? studentClass.getId() : null,
                studentClass != null ? studentClass.getName() : "Chưa có lớp",
                student.getEnrollmentYear(),
                student.getAvatarUrl(),
                student.getAcademicStatus()
        );
    }
}