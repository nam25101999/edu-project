package com.edu.university.modules.system.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.system.dto.response.DashboardStatsResponse;
import com.edu.university.modules.system.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<BaseResponse<DashboardStatsResponse>> getStats() {
        return ResponseEntity.ok(BaseResponse.ok("Tải thông số thống kê thành công", dashboardService.getStats()));
    }
}
