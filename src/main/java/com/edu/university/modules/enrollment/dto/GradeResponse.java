package com.edu.university.modules.enrollment.dto;

import java.util.UUID;

/**
 * DTO (Data Transfer Object) phản hồi kết quả sau khi nhập điểm hoặc lấy danh sách điểm.
 * Dùng để chuyển đổi từ Entity sang một đối tượng thuần dữ liệu,
 * giúp tránh lỗi LazyInitializationException (no Session) khi trả về JSON.
 */
public record GradeResponse(
        UUID id,
        UUID enrollmentId,

        // Thông tin sinh viên (Phẳng hóa để tránh lỗi Lazy Load Major)
        String studentCode,
        String studentFullName,
        String majorName,

        // Thông tin điểm số
        Double attendanceScore, // Điểm chuyên cần
        Double midtermScore,    // Điểm giữa kỳ
        Double finalScore,      // Điểm cuối kỳ
        Double totalScore,      // Điểm tổng kết (hệ 10)
        Double gpaScore,        // Điểm hệ 4
        String letterGrade      // Điểm chữ (A, B, C, D, F)
) {}