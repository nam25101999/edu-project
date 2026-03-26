package com.edu.university.modules.enrollment.dto;

import java.util.UUID;

/**
 * DTO dùng để tính toán GPA và kiểm tra môn tiên quyết.
 * Sử dụng record để tối ưu hiệu suất và tránh lỗi Lazy Loading.
 */
public record GradeGpaDto(
        UUID courseId,
        Double gpaScore,
        Double totalScore,
        Integer credits
) {}