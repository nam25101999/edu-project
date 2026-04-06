package com.edu.university.modules.finance.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.finance.entity.Scholarship;
import com.edu.university.modules.finance.service.ScholarshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scholarships")
@RequiredArgsConstructor
public class ScholarshipController {

    private final ScholarshipService scholarshipService;

    @PostMapping("/grant")
    public ResponseEntity<ApiResponse<Scholarship>> grantScholarship(
            @RequestParam UUID studentId,
            @RequestParam UUID semesterId,
            @RequestParam String name,
            @RequestParam BigDecimal amount
    ) {
        Scholarship scholarship = scholarshipService.grantScholarship(studentId, semesterId, name, amount);
        return ResponseEntity.ok(ApiResponse.success("Cấp học bổng thành công", scholarship));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<Scholarship>>> getStudentScholarships(@PathVariable UUID studentId) {
        List<Scholarship> scholarships = scholarshipService.getStudentScholarships(studentId);
        return ResponseEntity.ok(ApiResponse.success(scholarships));
    }
}
