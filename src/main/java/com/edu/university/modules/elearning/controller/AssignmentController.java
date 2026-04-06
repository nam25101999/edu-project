package com.edu.university.modules.elearning.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.elearning.dto.request.AssignmentRequest;
import com.edu.university.modules.elearning.entity.Assignment;
import com.edu.university.modules.elearning.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<BaseResponse<Assignment>> createAssignment(@RequestBody AssignmentRequest request) {
        Assignment assignment = assignmentService.createAssignment(
                request.getCourseSectionId(),
                request.getTitle(),
                request.getDescription(),
                request.getDueDate(),
                request.getMaxScore(),
                request.getAttachmentUrl()
        );
        return ResponseEntity.ok(BaseResponse.ok("Tạo bài tập thành công", assignment));
    }

    @GetMapping("/course-section/{id}")
    public ResponseEntity<BaseResponse<List<Assignment>>> getAssignmentsByCourseSection(@PathVariable UUID id) {
        List<Assignment> assignments = assignmentService.getAssignmentsByCourseSection(id);
        return ResponseEntity.ok(BaseResponse.ok(assignments));
    }
}
