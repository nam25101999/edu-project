package com.edu.university.modules.report.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.report.dto.ReportDtos.*;
import com.edu.university.modules.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý các báo cáo tổng hợp và Dashboard cho Quản trị viên.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    public ApiResponse<DashboardOverview> getDashboardOverview() {
        return ApiResponse.success("Lấy dữ liệu tổng quan thành công", reportService.getDashboardOverview());
    }

    @GetMapping("/faculty-stats")
    public ApiResponse<List<FacultyStat>> getStudentsByFaculty() {
        return ApiResponse.success("Thống kê sinh viên theo khoa thành công", reportService.getStudentsByFaculty());
    }

    @GetMapping("/pass-fail-ratio")
    public ApiResponse<PassFailStat> getPassFailRatio() {
        return ApiResponse.success("Thống kê tỷ lệ đậu/rớt thành công", reportService.getPassFailRatio());
    }

    @GetMapping("/top-students")
    public ApiResponse<List<TopStudent>> getTopStudents(@RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.success("Lấy danh sách top sinh viên thành công", reportService.getTopStudents(limit));
    }
}