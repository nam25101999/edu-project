package com.edu.university.modules.examination.controller;
 
import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.examination.dto.request.ExamRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamResponseDTO;
import com.edu.university.modules.examination.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.UUID;
 
@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {
 
    private final ExamService examService;
 
    @PostMapping
    public ResponseEntity<ApiResponse<ExamResponseDTO>> create(@Valid @RequestBody ExamRequestDTO requestDTO) {
        ExamResponseDTO exam = examService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Tạo lịch thi thành công", exam));
    }
 
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ExamResponseDTO>>> getAll(@PageableDefault(size = 100) Pageable pageable) {
        Page<ExamResponseDTO> page = examService.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(examService.getById(id)));
    }
 
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody ExamRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(examService.update(id, requestDTO)));
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        examService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
