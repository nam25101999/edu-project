package com.edu.university.service;

import com.edu.university.annotation.LogAction;
import com.edu.university.entity.AcademicStatus;
import com.edu.university.entity.Student;
import com.edu.university.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicService {

    private final StudentRepository studentRepo;
    private final GradeService gradeService;

    /**
     * Hàm này có thể được gọi tự động (Cron Job) vào cuối mỗi kỳ học
     * để đánh giá lại toàn bộ tình trạng học vụ của sinh viên.
     */
    @LogAction(action = "EVALUATE_ACADEMIC_STATUS", entityName = "STUDENT")
    @Transactional
    public void evaluateAllStudentsAcademicStatus() {
        List<Student> students = studentRepo.findAll();

        for (Student student : students) {
            double cumulativeGPA = gradeService.calculateCumulativeGPA(student.getId());

            // Xếp loại học vụ dựa trên GPA Hệ 4
            if (cumulativeGPA < 1.0) {
                student.setAcademicStatus(AcademicStatus.DINH_CHI); // Quá yếu -> Cấm đăng ký
            } else if (cumulativeGPA < 2.0) {
                student.setAcademicStatus(AcademicStatus.CANH_BAO); // Yếu -> Cảnh báo học vụ (bị giới hạn tín chỉ)
            } else {
                student.setAcademicStatus(AcademicStatus.BINH_THUONG); // Đủ điều kiện
            }

            studentRepo.save(student);
        }
        System.out.println("Đã hoàn thành đợt xét duyệt và cập nhật cảnh báo học vụ.");
    }
}