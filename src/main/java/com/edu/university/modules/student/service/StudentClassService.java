package com.edu.university.modules.student.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.report.annotation.LogAction;
import com.edu.university.modules.student.dto.StudentClassDtos.StudentClassRequest;
import com.edu.university.modules.student.dto.StudentClassDtos.StudentClassResponse;
import com.edu.university.modules.student.entity.Major;
import com.edu.university.modules.student.entity.StudentClass;
import com.edu.university.modules.student.repository.MajorRepository;
import com.edu.university.modules.student.repository.StudentClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentClassService {

    private final StudentClassRepository studentClassRepo;
    private final MajorRepository majorRepo;

    // ================== GET ALL ==================
    public List<StudentClassResponse> getAllClasses() {
        return studentClassRepo.findAllDto();
    }

    // ================== SEARCH + PAGINATION ==================
    public Page<StudentClassResponse> searchStudentClasses(String keyword, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("classCode").ascending());

        return studentClassRepo.searchStudentClasses(keyword, pageable);
    }

    // ================== GET BY ID ==================
    public StudentClassResponse getById(UUID id) {
        return studentClassRepo.findByIdDto(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.DATA_NOT_FOUND,
                        "Không tìm thấy lớp sinh hoạt"
                ));
    }

    // ================== CREATE ==================
    @LogAction(action = "CREATE_STUDENT_CLASS", entityName = "STUDENT_CLASS")
    @Transactional
    public StudentClassResponse createStudentClass(StudentClassRequest request) {

        if (studentClassRepo.existsByClassCode(request.classCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã lớp đã tồn tại");
        }

        Major major = majorRepo.findById(request.majorId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.DATA_NOT_FOUND,
                        "Không tìm thấy ngành"
                ));

        StudentClass sc = StudentClass.builder()
                .classCode(request.classCode())
                .name(request.name())
                .major(major)
                .build();

        studentClassRepo.save(sc);

        // 👉 gọi lại DTO để đảm bảo dữ liệu chuẩn
        return studentClassRepo.findByIdDto(sc.getId()).get();
    }

    // ================== UPDATE ==================
    @LogAction(action = "UPDATE_STUDENT_CLASS", entityName = "STUDENT_CLASS")
    @Transactional
    public StudentClassResponse updateStudentClass(UUID id, StudentClassRequest request) {

        StudentClass sc = studentClassRepo.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.DATA_NOT_FOUND,
                        "Không tìm thấy lớp"
                ));

        if (!sc.getClassCode().equals(request.classCode()) &&
                studentClassRepo.existsByClassCode(request.classCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã lớp đã tồn tại");
        }

        Major major = majorRepo.findById(request.majorId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.DATA_NOT_FOUND,
                        "Không tìm thấy ngành"
                ));

        sc.setClassCode(request.classCode());
        sc.setName(request.name());
        sc.setMajor(major);

        studentClassRepo.save(sc);

        return studentClassRepo.findByIdDto(sc.getId()).get();
    }

    // ================== DELETE ==================
    @LogAction(action = "DELETE_STUDENT_CLASS", entityName = "STUDENT_CLASS")
    @Transactional
    public void deleteStudentClass(UUID id) {

        if (!studentClassRepo.existsById(id)) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy lớp để xóa");
        }

        studentClassRepo.deleteById(id);
    }
}