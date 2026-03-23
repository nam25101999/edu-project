package com.edu.university.modules.student.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.report.annotation.LogAction;
import com.edu.university.modules.student.dto.StudentClassDtos.StudentClassRequest;
import com.edu.university.modules.student.entity.Major;
import com.edu.university.modules.student.entity.StudentClass;
import com.edu.university.modules.student.repository.MajorRepository;
import com.edu.university.modules.student.repository.StudentClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service quản lý lớp sinh hoạt của sinh viên.
 */
@Service
@RequiredArgsConstructor
public class StudentClassService {

    private final StudentClassRepository studentClassRepo;
    private final MajorRepository majorRepo;

    public List<StudentClass> getAllClasses() {
        return studentClassRepo.findAll();
    }

    @LogAction(action = "CREATE_STUDENT_CLASS", entityName = "STUDENT_CLASS")
    @Transactional
    public StudentClass createStudentClass(StudentClassRequest request) {
        if (studentClassRepo.existsByClassCode(request.classCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã lớp sinh hoạt đã tồn tại");
        }

        Major major = majorRepo.findById(request.majorId())
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy ngành học liên kết"));

        StudentClass studentClass = StudentClass.builder()
                .classCode(request.classCode())
                .name(request.name())
                .major(major)
                .build();

        return studentClassRepo.save(studentClass);
    }
}