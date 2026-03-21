package com.edu.university.modules.report.dto;

import java.util.UUID;

public class ReportDtos {

    // 1. DTO Thống kê sinh viên theo khoa
    public record FacultyStat(String faculty, Long count) {}

    // 2. DTO Thống kê tỷ lệ đậu rớt
    public record PassFailStat(
            Long passCount,
            Long failCount,
            Double passRate,
            Double failRate
    ) {}

    // 3. DTO Top sinh viên GPA cao
    public record TopStudent(
            UUID studentId,
            String studentCode,
            String fullName,
            Double gpa
    ) {}

    // 4. DTO Dashboard tổng quan
    public record DashboardOverview(
            Long totalStudents,
            Long totalLecturers,
            Long totalCourses,
            Long totalClasses
    ) {}
}