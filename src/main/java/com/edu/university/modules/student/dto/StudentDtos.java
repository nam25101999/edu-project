package com.edu.university.modules.student.dto;

import com.edu.university.modules.enrollment.entity.AcademicStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class StudentDtos {

    /**
     * DTO dùng cho API Create và Update sinh viên
     */
    public record StudentRequest(
            @NotBlank(message = "Mã sinh viên không được để trống")
            String studentCode,

            @NotBlank(message = "Họ và tên không được để trống")
            String fullName,

            @NotNull(message = "Ngành học (majorId) không được để trống")
            UUID majorId,

            @NotNull(message = "Lớp sinh hoạt (studentClassId) không được để trống")
            UUID studentClassId,

            @NotNull(message = "Năm nhập học không được để trống")
            Integer enrollmentYear,

            String avatarUrl,

            AcademicStatus academicStatus
    ) {}

    /**
     * DTO phản hồi thông tin sinh viên.
     * Đã cập nhật UUID và bổ sung facultyName để hiển thị đầy đủ thông tin Ngành & Khoa.
     */
    public record StudentResponse(
            UUID id,
            String studentCode,
            String fullName,
            UUID majorId,       // Đồng bộ kiểu dữ liệu UUID
            String majorName,
            String facultyName, // Bổ sung thông tin Khoa
            UUID studentClassId, // Đồng bộ kiểu dữ liệu UUID
            String className,
            Integer enrollmentYear,
            String avatarUrl,
            AcademicStatus academicStatus
    ) {}
}