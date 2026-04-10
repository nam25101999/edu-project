package com.edu.university.modules.elearning.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.elearning.dto.request.AssignmentRequest;
import com.edu.university.modules.elearning.dto.response.AssignmentResponseDTO;
import com.edu.university.modules.elearning.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AssignmentResponseDTO>> createAssignment(@RequestBody AssignmentRequest request) {
        AssignmentResponseDTO assignment = assignmentService.createAssignment(
                request.getCourseSectionId(),
                request.getTitle(),
                request.getDescription(),
                request.getDueDate(),
                request.getMaxScore(),
                request.getAttachmentUrl()
        );
        return ResponseEntity.ok(ApiResponse.success("Táº¡o bÃ i táº­p thÃ nh cÃ´ng", assignment));
    }

    @GetMapping("/course-section/{id}")
    public ResponseEntity<ApiResponse<List<AssignmentResponseDTO>>> getAssignmentsByCourseSection(
            @PathVariable UUID id,
            Pageable pageable) {
        List<AssignmentResponseDTO> list = assignmentService.getAssignmentsByCourseSection(id, pageable).getContent();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
