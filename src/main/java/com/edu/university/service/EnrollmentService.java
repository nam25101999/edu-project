package com.edu.university.service;

import com.edu.university.entity.*;
import com.edu.university.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepo;
    private final ClassSectionRepository classSectionRepo;
    private final StudentRepository studentRepo;
    private final GradeRepository gradeRepo;
    private final TuitionFeeRepository tuitionFeeRepo; // Thêm repo học phí

    @Transactional
    public Enrollment enroll(UUID studentId, UUID classSectionId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        ClassSection section = classSectionRepo.findById(classSectionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần"));

        if (enrollmentRepo.existsByStudentIdAndClassSectionId(studentId, classSectionId)) {
            throw new RuntimeException("Sinh viên đã đăng ký lớp học phần này rồi");
        }

        // ==========================================
        // KHỐI 1: KIỂM TRA ĐIỀU KIỆN ĐĂNG KÝ NÂNG CAO
        // ==========================================

        // 1. Kiểm tra THỜI HẠN ĐĂNG KÝ
        LocalDateTime now = LocalDateTime.now();
        if (section.getRegistrationStartDate() != null && now.isBefore(section.getRegistrationStartDate())) {
            throw new RuntimeException("Chưa đến thời gian đăng ký tín chỉ cho môn này.");
        }
        if (section.getRegistrationEndDate() != null && now.isAfter(section.getRegistrationEndDate())) {
            throw new RuntimeException("Đã hết hạn đăng ký tín chỉ.");
        }

        // 2. Kiểm tra CẢNH BÁO HỌC VỤ
        if (student.getAcademicStatus() == AcademicStatus.DINH_CHI) {
            throw new RuntimeException("Bạn đang bị đình chỉ hoặc cấm đăng ký do điểm học vụ quá thấp.");
        }

        // 3. Kiểm tra NỢ HỌC PHÍ
        boolean hasDebt = tuitionFeeRepo.findByStudentId(studentId).stream()
                .anyMatch(fee -> fee.getStatus() != TuitionStatus.DA_DONG);
        if (hasDebt) {
            throw new RuntimeException("Bạn chưa hoàn thành học phí các kỳ trước. Vui lòng thanh toán để tiếp tục đăng ký.");
        }

        // 4. Kiểm tra GIỚI HẠN TÍN CHỈ trong kỳ (VD: Bình thường max 24 TC, Cảnh báo max 14 TC)
        int currentSemesterCredits = enrollmentRepo.findByStudentId(studentId).stream()
                .filter(e -> e.getClassSection().getSemester().equals(section.getSemester()) &&
                        e.getClassSection().getYear().equals(section.getYear()))
                .mapToInt(e -> e.getClassSection().getCourse().getCredits())
                .sum();

        int maxCredits = (student.getAcademicStatus() == AcademicStatus.CANH_BAO) ? 14 : 24;
        if (currentSemesterCredits + section.getCourse().getCredits() > maxCredits) {
            throw new RuntimeException("Vượt quá số tín chỉ tối đa cho phép trong kỳ này (" + maxCredits + " TC).");
        }


        // ==========================================
        // KHỐI 2: LOGIC CỐT LÕI (HỌC LẠI, TRÙNG LỊCH)
        // ==========================================

        // 5. Nhận diện học lại / cải thiện điểm
        UUID courseId = section.getCourse().getId();
        List<Grade> previousGrades = gradeRepo.findByEnrollmentStudentId(studentId).stream()
                .filter(g -> g.getEnrollment().getClassSection().getCourse().getId().equals(courseId))
                .toList();

        if (!previousGrades.isEmpty()) {
            double maxPreviousScore = previousGrades.stream()
                    .filter(g -> g.getTotalScore() != null)
                    .mapToDouble(Grade::getTotalScore)
                    .max().orElse(0.0);

            if (maxPreviousScore < 4.0) {
                System.out.println("Ghi nhận: Sinh viên đăng ký học lại môn rớt - " + section.getCourse().getName());
            } else {
                System.out.println("Ghi nhận: Sinh viên đăng ký học cải thiện - " + section.getCourse().getName());
            }
        }

        // 6. Kiểm tra số lượng sinh viên tối đa của lớp
        long currentEnrolled = enrollmentRepo.countByClassSectionId(classSectionId);
        if (currentEnrolled >= section.getMaxStudents()) {
            throw new RuntimeException("Lớp học phần đã đầy");
        }

        // 7. Kiểm tra trùng lịch học
        List<Enrollment> currentEnrollments = enrollmentRepo.findByStudentId(studentId);
        boolean scheduleClash = currentEnrollments.stream()
                .anyMatch(e -> e.getClassSection().getSchedule().equals(section.getSchedule()) &&
                        e.getClassSection().getSemester().equals(section.getSemester()) &&
                        e.getClassSection().getYear().equals(section.getYear()));
        if (scheduleClash) {
            throw new RuntimeException("Phát hiện trùng lịch học với môn khác trong học kỳ này");
        }

        // 8. Kiểm tra điều kiện tiên quyết
        Course prereq = section.getCourse().getPrerequisiteCourse();
        if (prereq != null) {
            boolean hasPassed = gradeRepo.findByEnrollmentStudentId(studentId).stream()
                    .anyMatch(g -> g.getEnrollment().getClassSection().getCourse().getId().equals(prereq.getId())
                            && g.getTotalScore() != null && g.getTotalScore() >= 4.0);
            if (!hasPassed) {
                throw new RuntimeException("Chưa đạt điều kiện môn tiên quyết: " + prereq.getName());
            }
        }

        // LƯU ĐĂNG KÝ
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .classSection(section)
                .enrollmentDate(LocalDateTime.now())
                .build();

        return enrollmentRepo.save(enrollment);
    }
}