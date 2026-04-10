package com.edu.university.modules.grading.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.grading.dto.request.StudentSummaryRequestDTO;
import com.edu.university.modules.grading.dto.response.StudentSummaryResponseDTO;
import com.edu.university.modules.grading.service.StudentSummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/student-summaries")
@RequiredArgsConstructor
public class StudentSummaryController {

    private final StudentSummaryService studentSummaryService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentSummaryResponseDTO>> upsert(@Valid @RequestBody StudentSummaryRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật tổng kết điểm thành công", studentSummaryService.upsert(requestDTO)));
    }

    @GetMapping("/registration/{registrationId}")
    public ResponseEntity<BaseResponse<StudentSummaryResponseDTO>> getByRegistrationId(@PathVariable UUID registrationId) {
        return ResponseEntity.ok(BaseResponse.ok(studentSummaryService.getByRegistrationId(registrationId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        studentSummaryService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa tổng kết điểm thành công", null));
    }
}
