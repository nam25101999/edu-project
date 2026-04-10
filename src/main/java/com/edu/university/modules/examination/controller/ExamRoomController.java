package com.edu.university.modules.examination.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.examination.dto.request.ExamRoomRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamRoomResponseDTO;
import com.edu.university.modules.examination.service.ExamRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/exam-rooms")
@RequiredArgsConstructor
public class ExamRoomController {

    private final ExamRoomService examRoomService;

    @PostMapping
    public ResponseEntity<BaseResponse<ExamRoomResponseDTO>> create(@Valid @RequestBody ExamRoomRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.created(examRoomService.create(requestDTO)));
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<BaseResponse<PageResponse<ExamRoomResponseDTO>>> getByExamId(@PathVariable UUID examId, Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(examRoomService.getByExamId(examId, pageable)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        examRoomService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok());
    }
}
