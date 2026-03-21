package com.edu.university.modules.enrollment.repository.course.entity;

public enum ClassSectionStatus {
    OPEN,       // Đang mở đăng ký
    CLOSED,     // Đã đóng (Đầy hoặc hết hạn)
    CANCELED    // Hủy lớp (Do quá ít sinh viên)
}