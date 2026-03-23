package com.edu.university.modules.course.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.report.annotation.LogAction;
import com.edu.university.modules.course.dto.ExamScheduleDtos.ExamScheduleRequest;
import com.edu.university.modules.course.entity.ClassSection;
import com.edu.university.modules.course.entity.ExamSchedule;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.course.repository.ClassSectionRepository;
import com.edu.university.modules.enrollment.repository.EnrollmentRepository;
import com.edu.university.modules.course.repository.ExamScheduleRepository;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.course.mapper.ExamScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service quản lý lịch thi.
 * Đã chuẩn hóa lỗi theo Enterprise Standard (ErrorCode EXM, STD, SYS).
 */
@Service
@RequiredArgsConstructor
public class ExamScheduleService {

    private final ExamScheduleRepository examRepo;
    private final ClassSectionRepository classSectionRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final StudentRepository studentRepo;
    private final ExamScheduleMapper examScheduleMapper;

    @LogAction(action = "CREATE_EXAM_SCHEDULE", entityName = "EXAM_SCHEDULE")
    @Transactional
    public ExamSchedule createExamSchedule(ExamScheduleRequest request) {
        // Kiểm tra lớp học phần
        ClassSection section = classSectionRepo.findById(request.classSectionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_SECTION_NOT_FOUND));

        // Kiểm tra logic thời gian
        if (!request.startTime().isBefore(request.endTime())) {
            throw new BusinessException(ErrorCode.INVALID_EXAM_TIME);
        }

        // 1. Kiểm tra trùng phòng thi (EXM_002)
        List<ExamSchedule> roomConflicts = examRepo.findOverlappingByRoom(request.room(), request.startTime(), request.endTime());
        if (!roomConflicts.isEmpty()) {
            throw new BusinessException(ErrorCode.EXAM_ROOM_CONFLICT,
                    "Phòng thi " + request.room() + " đã có lịch thi khác trong khung giờ này.");
        }

        // 2. Kiểm tra trùng lịch thi của SINH VIÊN (EXM_003)
        List<UUID> studentIdsInClass = enrollmentRepo.findByClassSectionId(request.classSectionId())
                .stream().map(e -> e.getStudent().getId()).toList();

        List<ExamSchedule> overlappingExams = examRepo.findOverlappingExams(request.startTime(), request.endTime());

        for (ExamSchedule exam : overlappingExams) {
            List<UUID> overlappingStudentIds = enrollmentRepo.findByClassSectionId(exam.getClassSection().getId())
                    .stream().map(e -> e.getStudent().getId()).toList();

            for (UUID studentId : studentIdsInClass) {
                if (overlappingStudentIds.contains(studentId)) {
                    Student student = studentRepo.findById(studentId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

                    throw new BusinessException(ErrorCode.EXAM_STUDENT_CONFLICT,
                            "Sinh viên " + student.getFullName() + " (" + student.getStudentCode() +
                                    ") đã có lịch thi môn " + exam.getClassSection().getCourse().getName());
                }
            }
        }

        ExamSchedule exam = examScheduleMapper.toEntity(request);
        exam.setClassSection(section);

        return examRepo.save(exam);
    }

    @LogAction(action = "VIEW_MY_EXAM_SCHEDULES", entityName = "EXAM_SCHEDULE")
    public List<ExamSchedule> getMyExamSchedules(UUID userId) {
        Student student = studentRepo.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

        List<UUID> myClassSectionIds = enrollmentRepo.findByStudentId(student.getId())
                .stream().map(e -> e.getClassSection().getId()).toList();

        if (myClassSectionIds.isEmpty()) return List.of();

        return examRepo.findByClassSectionIdInOrderByStartTimeAsc(myClassSectionIds);
    }
}