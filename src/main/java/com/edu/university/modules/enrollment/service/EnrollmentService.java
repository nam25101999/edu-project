package com.edu.university.modules.enrollment.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.enrollment.repository.EnrollmentRepository;
import com.edu.university.modules.enrollment.repository.GradeRepository;
import com.edu.university.modules.course.repository.ClassSectionRepository;
import com.edu.university.modules.finance.repository.TuitionFeeRepository;
import com.edu.university.modules.report.annotation.LogAction;
import com.edu.university.modules.course.entity.ClassSection;
import com.edu.university.modules.course.entity.Course;
import com.edu.university.modules.enrollment.entity.AcademicStatus;
import com.edu.university.modules.enrollment.entity.Enrollment;
import com.edu.university.modules.enrollment.entity.Grade;
import com.edu.university.modules.finance.entity.TuitionStatus;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service xử lý nghiệp vụ đăng ký học phần (Tín chỉ).
 * Đã chuẩn hóa lỗi theo Enterprise Standard (ErrorCode ENR, CRS, STD).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepo;
    private final ClassSectionRepository classSectionRepo;
    private final StudentRepository studentRepo;
    private final GradeRepository gradeRepo;
    private final TuitionFeeRepository tuitionFeeRepo;

    @LogAction(action = "ENROLL_COURSE", entityName = "ENROLLMENT")
    @Transactional
    public Enrollment enroll(UUID studentId, UUID classSectionId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

        ClassSection section = classSectionRepo.findById(classSectionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_SECTION_NOT_FOUND));

        if (enrollmentRepo.existsByStudentIdAndClassSectionId(studentId, classSectionId)) {
            throw new BusinessException(ErrorCode.ALREADY_ENROLLED, "Sinh viên đã đăng ký lớp học phần này rồi");
        }

        // 1. Kiểm tra THỜI HẠN ĐĂNG KÝ
        LocalDateTime now = LocalDateTime.now();
        if (section.getRegistrationStartDate() != null && now.isBefore(section.getRegistrationStartDate())) {
            throw new BusinessException(ErrorCode.REGISTRATION_NOT_STARTED);
        }
        if (section.getRegistrationEndDate() != null && now.isAfter(section.getRegistrationEndDate())) {
            throw new BusinessException(ErrorCode.REGISTRATION_ENDED);
        }

        // 2. Kiểm tra CẢNH BÁO HỌC VỤ
        if (student.getAcademicStatus() == AcademicStatus.DINH_CHI) {
            throw new BusinessException(ErrorCode.ACADEMIC_SUSPENSION, "Bạn đang bị đình chỉ hoặc cấm đăng ký do điểm học vụ quá thấp.");
        }

        // 3. Kiểm tra NỢ HỌC PHÍ (Nếu có logic tài chính)
        boolean hasDebt = tuitionFeeRepo.findByStudentId(studentId).stream()
                .anyMatch(fee -> fee.getStatus() != TuitionStatus.DA_DONG);
        if (hasDebt) {
            throw new BusinessException(ErrorCode.TUITION_DEBT, "Bạn chưa hoàn thành học phí các kỳ trước.");
        }

        // 4. Kiểm tra GIỚI HẠN TÍN CHỈ
        int currentSemesterCredits = enrollmentRepo.findByStudentId(studentId).stream()
                .filter(e -> e.getClassSection().getSemester().equals(section.getSemester()) &&
                        e.getClassSection().getYear().equals(section.getYear()))
                .mapToInt(e -> e.getClassSection().getCourse().getCredits())
                .sum();

        int maxCredits = (student.getAcademicStatus() == AcademicStatus.CANH_BAO) ? 14 : 24;
        if (currentSemesterCredits + section.getCourse().getCredits() > maxCredits) {
            throw new BusinessException(ErrorCode.CREDIT_LIMIT_EXCEEDED, "Vượt quá số tín chỉ tối đa cho phép (" + maxCredits + " TC).");
        }

        // 5. Kiểm tra số lượng sinh viên tối đa
        long currentEnrolled = enrollmentRepo.countByClassSectionId(classSectionId);
        if (currentEnrolled >= section.getMaxStudents()) {
            throw new BusinessException(ErrorCode.CLASS_FULL);
        }

        // 6. Kiểm tra trùng lịch học
        boolean scheduleClash = enrollmentRepo.findByStudentId(studentId).stream()
                .anyMatch(e -> e.getClassSection().getSchedule().equals(section.getSchedule()) &&
                        e.getClassSection().getSemester().equals(section.getSemester()) &&
                        e.getClassSection().getYear().equals(section.getYear()));
        if (scheduleClash) {
            throw new BusinessException(ErrorCode.SCHEDULE_CONFLICT);
        }

        // 7. Kiểm tra điều kiện tiên quyết
        Course prereq = section.getCourse().getPrerequisiteCourse();
        if (prereq != null) {
            boolean hasPassed = gradeRepo.findByEnrollmentStudentId(studentId).stream()
                    .anyMatch(g -> g.getEnrollment().getClassSection().getCourse().getId().equals(prereq.getId())
                            && g.getTotalScore() != null && g.getTotalScore() >= 4.0);
            if (!hasPassed) {
                throw new BusinessException(ErrorCode.PREREQUISITE_NOT_MET, "Chưa đạt điều kiện môn tiên quyết: " + prereq.getName());
            }
        }

        // LƯU ĐĂNG KÝ
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .classSection(section)
                .enrollmentDate(LocalDateTime.now())
                .build();

        log.info("Sinh viên {} đã đăng ký thành công lớp {}", student.getStudentCode(), section.getId());
        return enrollmentRepo.save(enrollment);
    }
}