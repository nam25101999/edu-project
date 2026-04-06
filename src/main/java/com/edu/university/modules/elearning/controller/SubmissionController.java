package com.edu.university.modules.elearning.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.elearning.dto.request.GradeRequest;
import com.edu.university.modules.elearning.dto.request.SubmissionRequest;
import com.edu.university.modules.elearning.entity.Submission;
import com.edu.university.modules.elearning.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<BaseResponse<Submission>> createSubmission(@RequestBody SubmissionRequest request) {
        Submission submission = submissionService.createSubmission(
                request.getAssignmentId(),
                request.getStudentId(),
                request.getContent(),
                request.getFileUrl()
        );
        return ResponseEntity.ok(BaseResponse.ok("Nộp bài thành công", submission));
    }

    @PutMapping("/{id}/grade")
    public ResponseEntity<BaseResponse<Submission>> gradeSubmission(
            @PathVariable UUID id,
            @RequestBody GradeRequest request
    ) {
        Submission submission = submissionService.gradeSubmission(id, request.getScore(), request.getFeedback());
        return ResponseEntity.ok(BaseResponse.ok("Chấm điểm thành công", submission));
    }
}
