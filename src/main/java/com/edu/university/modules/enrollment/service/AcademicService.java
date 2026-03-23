package com.edu.university.modules.enrollment.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.report.annotation.LogAction;
import com.edu.university.modules.enrollment.entity.AcademicStatus;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service xử lý các nghiệp vụ đánh giá học vụ sinh viên.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AcademicService {

    private final StudentRepository studentRepo;
    private final GradeService gradeService;

    /**
     * Đánh giá lại toàn bộ tình trạng học vụ của sinh viên dựa trên GPA tích lũy.
     */
    @LogAction(action = "EVALUATE_ACADEMIC_STATUS", entityName = "STUDENT")
    @Transactional
    public void evaluateAllStudentsAcademicStatus() {
        List<Student> students = studentRepo.findAll();

        for (Student student : students) {
            double cumulativeGPA = gradeService.calculateCumulativeGPA(student.getId());

            // Xếp loại học vụ dựa trên GPA Hệ 4
            if (cumulativeGPA < 1.0) {
                student.setAcademicStatus(AcademicStatus.DINH_CHI);
            } else if (cumulativeGPA < 2.0) {
                student.setAcademicStatus(AcademicStatus.CANH_BAO);
            } else {
                student.setAcademicStatus(AcademicStatus.BINH_THUONG);
            }

            studentRepo.save(student);
        }
        log.info("Đã hoàn thành đợt xét duyệt và cập nhật cảnh báo học vụ cho {} sinh viên.", students.size());
    }
}