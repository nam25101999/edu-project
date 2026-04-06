package com.edu.university.modules.elearning.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.elearning.dto.request.AttendanceRequest;
import com.edu.university.modules.elearning.entity.Attendance;
import com.edu.university.modules.elearning.entity.AttendanceRecord;
import com.edu.university.modules.elearning.repository.AttendanceRecordRepository;
import com.edu.university.modules.elearning.repository.AttendanceRepository;
import com.edu.university.modules.schedule.entity.Schedule;
import com.edu.university.modules.schedule.repository.ScheduleRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public Attendance createAttendance(AttendanceRequest request) {
        CourseSection courseSection = courseSectionRepository.findById(request.getCourseSectionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_SECTION_NOT_FOUND));

        Schedule schedule = null;
        if (request.getScheduleId() != null) {
            schedule = scheduleRepository.findById(request.getScheduleId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND));
        }

        Attendance attendance = Attendance.builder()
                .courseSection(courseSection)
                .schedule(schedule)
                .attendanceDate(request.getAttendanceDate())
                .notes(request.getNotes())
                .build();

        Attendance savedAttendance = attendanceRepository.save(attendance);

        if (request.getRecords() != null) {
            for (AttendanceRequest.AttendanceRecordRequest recordRequest : request.getRecords()) {
                Student student = studentRepository.findById(recordRequest.getStudentId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

                AttendanceRecord record = AttendanceRecord.builder()
                        .attendance(savedAttendance)
                        .student(student)
                        .status(recordRequest.getStatus())
                        .note(recordRequest.getNote())
                        .build();
                
                attendanceRecordRepository.save(record);
            }
        }

        return savedAttendance;
    }

    public List<Attendance> getAttendanceByCourseSection(UUID courseSectionId) {
        return attendanceRepository.findByCourseSectionId(courseSectionId);
    }

    public List<AttendanceRecord> getStudentAttendanceHistory(UUID courseSectionId, UUID studentId) {
        return attendanceRecordRepository.findByAttendance_CourseSectionIdAndStudentId(courseSectionId, studentId);
    }
}
