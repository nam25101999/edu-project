package com.edu.university.modules.elearning.repository;

import com.edu.university.modules.elearning.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, UUID> {
    List<AttendanceRecord> findByAttendanceId(UUID attendanceId);
    List<AttendanceRecord> findByStudentId(UUID studentId);
    List<AttendanceRecord> findByAttendance_CourseSectionIdAndStudentId(UUID courseSectionId, UUID studentId);
}
