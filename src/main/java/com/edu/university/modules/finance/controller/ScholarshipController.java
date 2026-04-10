package com.edu.university.modules.finance.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.finance.dto.response.ScholarshipResponseDTO;
import com.edu.university.modules.finance.service.ScholarshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/scholarships")
@RequiredArgsConstructor
public class ScholarshipController {

    private final ScholarshipService scholarshipService;

    @PostMapping("/grant")
    public ResponseEntity<BaseResponse<ScholarshipResponseDTO>> grantScholarship(
            @RequestParam UUID studentId,
            @RequestParam UUID semesterId,
            @RequestParam String name,
            @RequestParam BigDecimal amount
    ) {
        return ResponseEntity.ok(BaseResponse.ok("Cấp học bổng thành công", 
                scholarshipService.grantScholarship(studentId, semesterId, name, amount)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<BaseResponse<PageResponse<ScholarshipResponseDTO>>> getStudentScholarships(
            @PathVariable UUID studentId,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(scholarshipService.getStudentScholarships(studentId, pageable)));
    }
}
