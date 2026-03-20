package com.edu.university.repository;

import com.edu.university.entity.ExamSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, UUID> {

    // Lấy lịch thi của một danh sách các lớp học phần
    List<ExamSchedule> findByClassSectionIdInOrderByStartTimeAsc(List<UUID> classSectionIds);

    // Tìm các lịch thi bị trùng thời gian tại MỘT PHÒNG CỤ THỂ
    @Query("SELECT e FROM ExamSchedule e WHERE e.room = :room AND e.startTime < :endTime AND e.endTime > :startTime")
    List<ExamSchedule> findOverlappingByRoom(
            @Param("room") String room,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // Tìm TẤT CẢ các lịch thi đang diễn ra trong một khoảng thời gian (Dùng để check trùng lịch sinh viên)
    @Query("SELECT e FROM ExamSchedule e WHERE e.startTime < :endTime AND e.endTime > :startTime")
    List<ExamSchedule> findOverlappingExams(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}