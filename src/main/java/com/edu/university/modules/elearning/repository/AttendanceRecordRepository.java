package com.edu.university.modules.elearning.repository;

import com.edu.university.modules.elearning.entity.AttendanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, UUID> {
    Page<AttendanceRecord> findByAttendanceId(UUID attendanceId, Pageable pageable);
    Page<AttendanceRecord> findByStudentId(UUID studentId, Pageable pageable);
    Page<AttendanceRecord> findByAttendance_CourseSectionIdAndStudentId(UUID courseSectionId, UUID studentId, Pageable pageable);
}
