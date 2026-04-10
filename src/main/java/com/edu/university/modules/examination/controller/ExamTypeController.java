package com.edu.university.modules.examination.controller;
 
import com.edu.university.modules.examination.dto.request.ExamTypeRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamTypeResponseDTO;
import com.edu.university.modules.examination.service.ExamTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.UUID;
 
@RestController
@RequestMapping("/api/exam-types")
@RequiredArgsConstructor
public class ExamTypeController {
 
    private final ExamTypeService examTypeService;
 
    @PostMapping
    public ResponseEntity<ExamTypeResponseDTO> create(@Valid @RequestBody ExamTypeRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(examTypeService.create(requestDTO));
    }
 
    @GetMapping
    public ResponseEntity<List<ExamTypeResponseDTO>> getAll(@PageableDefault(size = 100) Pageable pageable) {
        return ResponseEntity.ok(examTypeService.getAll(pageable).getContent());
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<ExamTypeResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(examTypeService.getById(id));
    }
 
    @PutMapping("/{id}")
    public ResponseEntity<ExamTypeResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody ExamTypeRequestDTO requestDTO) {
        return ResponseEntity.ok(examTypeService.update(id, requestDTO));
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        examTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
