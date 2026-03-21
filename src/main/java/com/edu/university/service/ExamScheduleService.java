package com.edu.university.service;

import com.edu.university.annotation.LogAction;
import com.edu.university.dto.ExamScheduleDtos.ExamScheduleRequest;
import com.edu.university.entity.*;
import com.edu.university.repository.ClassSectionRepository;
import com.edu.university.repository.EnrollmentRepository;
import com.edu.university.repository.ExamScheduleRepository;
import com.edu.university.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExamScheduleService {

    private final ExamScheduleRepository examRepo;
    private final ClassSectionRepository classSectionRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final StudentRepository studentRepo;

    @LogAction(action = "CREATE_EXAM_SCHEDULE", entityName = "EXAM_SCHEDULE")
    @Transactional
    public ExamSchedule createExamSchedule(ExamScheduleRequest request) {
        ClassSection section = classSectionRepo.findById(request.classSectionId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần"));

        if (!request.startTime().isBefore(request.endTime())) {
            throw new RuntimeException("Thời gian kết thúc phải sau thời gian bắt đầu");
        }

        // 1. Kiểm tra trùng phòng thi
        List<ExamSchedule> roomConflicts = examRepo.findOverlappingByRoom(request.room(), request.startTime(), request.endTime());
        if (!roomConflicts.isEmpty()) {
            throw new RuntimeException("Phòng thi " + request.room() + " đã có lịch thi khác trong khung giờ này.");
        }

        // 2. Kiểm tra trùng lịch thi của SINH VIÊN
        List<UUID> studentIdsInClass = enrollmentRepo.findByClassSectionId(request.classSectionId())
                .stream().map(e -> e.getStudent().getId()).toList();

        List<ExamSchedule> overlappingExams = examRepo.findOverlappingExams(request.startTime(), request.endTime());

        for (ExamSchedule exam : overlappingExams) {
            List<UUID> overlappingStudentIds = enrollmentRepo.findByClassSectionId(exam.getClassSection().getId())
                    .stream().map(e -> e.getStudent().getId()).toList();

            for (UUID studentId : studentIdsInClass) {
                if (overlappingStudentIds.contains(studentId)) {
                    Student student = studentRepo.findById(studentId).get();
                    throw new RuntimeException("Phát hiện trùng lịch thi! Sinh viên " + student.getFullName() +
                            " (" + student.getStudentCode() + ") đã có lịch thi môn " +
                            exam.getClassSection().getCourse().getName() + " vào thời gian này.");
                }
            }
        }

        ExamSchedule exam = ExamSchedule.builder()
                .classSection(section)
                .examType(request.examType())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .room(request.room())
                .build();

        return examRepo.save(exam);
    }

    // Sinh viên xem lịch thi cá nhân
    public List<ExamSchedule> getMyExamSchedules(UUID userId) {
        Student student = studentRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        List<UUID> myClassSectionIds = enrollmentRepo.findByStudentId(student.getId())
                .stream().map(e -> e.getClassSection().getId()).toList();

        if (myClassSectionIds.isEmpty()) return List.of();

        return examRepo.findByClassSectionIdInOrderByStartTimeAsc(myClassSectionIds);
    }
}