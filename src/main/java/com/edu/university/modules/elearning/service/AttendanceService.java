package com.edu.university.modules.elearning.service;

import com.edu.university.common.exception.AppException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.elearning.dto.request.AttendanceRequest;
import com.edu.university.modules.elearning.dto.response.AttendanceRecordResponseDTO;
import com.edu.university.modules.elearning.dto.response.AttendanceResponseDTO;
import com.edu.university.modules.elearning.entity.Attendance;
import com.edu.university.modules.elearning.entity.AttendanceRecord;
import com.edu.university.modules.elearning.mapper.AttendanceMapper;
import com.edu.university.modules.elearning.repository.AttendanceRecordRepository;
import com.edu.university.modules.elearning.repository.AttendanceRepository;
import com.edu.university.modules.schedule.entity.Schedule;
import com.edu.university.modules.schedule.repository.ScheduleRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudentRepository studentRepository;
    private final AttendanceMapper attendanceMapper;

    @Transactional
    public AttendanceResponseDTO createAttendance(AttendanceRequest request) {
        CourseSection courseSection = courseSectionRepository.findById(request.getCourseSectionId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_SECTION_NOT_FOUND));

        Schedule schedule = null;
        if (request.getScheduleId() != null) {
            schedule = scheduleRepository.findById(request.getScheduleId())
                    .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        }

        Attendance attendance = Attendance.builder()
                .courseSection(courseSection)
                .schedule(schedule)
                .attendanceDate(request.getAttendanceDate())
                .notes(request.getNotes())
                .isActive(true)
                .build();

        Attendance savedAttendance = attendanceRepository.save(attendance);

        if (request.getRecords() != null) {
            for (AttendanceRequest.AttendanceRecordRequest recordRequest : request.getRecords()) {
                Student student = studentRepository.findById(recordRequest.getStudentId())
                        .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

                AttendanceRecord record = AttendanceRecord.builder()
                        .attendance(savedAttendance)
                        .student(student)
                        .status(recordRequest.getStatus())
                        .note(recordRequest.getNote())
                        .isActive(true)
                        .build();
                
                attendanceRecordRepository.save(record);
            }
        }

        return attendanceMapper.toResponseDTO(savedAttendance);
    }

    public Page<AttendanceResponseDTO> getAttendanceByCourseSection(UUID courseSectionId, Pageable pageable) {
        return attendanceRepository.findByCourseSectionId(courseSectionId, pageable)
                .map(attendanceMapper::toResponseDTO);
    }

    public Page<AttendanceRecordResponseDTO> getStudentAttendanceHistory(UUID courseSectionId, UUID studentId, Pageable pageable) {
        return attendanceRecordRepository.findByAttendance_CourseSectionIdAndStudentId(courseSectionId, studentId, pageable)
                .map(attendanceMapper::toRecordResponseDTO);
    }
}
