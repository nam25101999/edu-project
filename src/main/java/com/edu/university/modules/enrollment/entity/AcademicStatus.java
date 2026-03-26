package com.edu.university.modules.enrollment.entity;

import lombok.Getter;

/**
 * Enum định nghĩa trạng thái học vụ của sinh viên.
 */
@Getter
public enum AcademicStatus {
    BINH_THUONG("Bình thường"),
    CANH_BAO("Cảnh báo học vụ"),
    DINH_CHI("Đình chỉ học tập"),
    BAO_LUU("Bảo lưu kết quả"),
    THOI_HOC("Thôi học"),
    TOT_NGHIEP("Đã tốt nghiệp");

    private final String description;

    AcademicStatus(String description) {
        this.description = description;
    }
}