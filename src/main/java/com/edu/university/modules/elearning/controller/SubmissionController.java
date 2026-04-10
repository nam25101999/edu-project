package com.edu.university.modules.elearning.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.elearning.dto.request.GradeRequest;
import com.edu.university.modules.elearning.dto.request.SubmissionRequest;
import com.edu.university.modules.elearning.dto.response.SubmissionResponseDTO;
import com.edu.university.modules.elearning.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<ApiResponse<SubmissionResponseDTO>> createSubmission(@RequestBody SubmissionRequest request) {
        SubmissionResponseDTO submission = submissionService.createSubmission(
                request.getAssignmentId(),
                request.getStudentId(),
                request.getContent(),
                request.getFileUrl()
        );
        return ResponseEntity.ok(ApiResponse.success("Táº¡o bÃ i ná»™p thÃ nh cÃ´ng", submission));
    }

    @PutMapping("/{id}/grade")
    public ResponseEntity<ApiResponse<SubmissionResponseDTO>> gradeSubmission(
            @PathVariable UUID id,
            @RequestBody GradeRequest request
    ) {
        SubmissionResponseDTO submission = submissionService.gradeSubmission(id, request.getScore(), request.getFeedback());
        return ResponseEntity.ok(ApiResponse.success("Cháº¥m Ä‘iá»ƒm thÃ nh cÃ´ng", submission));
    }

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<ApiResponse<PageResponse<SubmissionResponseDTO>>> getSubmissionsByAssignment(
            @PathVariable UUID assignmentId,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(submissionService.getSubmissionsByAssignment(assignmentId, pageable))));
    }
}
