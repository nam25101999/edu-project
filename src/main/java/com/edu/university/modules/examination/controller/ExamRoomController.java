package com.edu.university.modules.examination.controller;

import com.edu.university.modules.examination.dto.request.ExamRoomRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamRoomResponseDTO;
import com.edu.university.modules.examination.service.ExamRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exam-rooms")
@RequiredArgsConstructor
public class ExamRoomController {

    private final ExamRoomService examRoomService;

    @PostMapping
    public ResponseEntity<ExamRoomResponseDTO> create(@Valid @RequestBody ExamRoomRequestDTO requestDTO) {
        return new ResponseEntity<>(examRoomService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<ExamRoomResponseDTO>> getByExamId(@PathVariable UUID examId) {
        return ResponseEntity.ok(examRoomService.getByExamId(examId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        examRoomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
