package com.edu.university.modules.student.controller;

import com.edu.university.modules.student.dto.request.AdvisorClassSectionRequestDTO;
import com.edu.university.modules.student.dto.response.AdvisorClassSectionResponseDTO;
import com.edu.university.modules.student.service.AdvisorClassSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/advisor-class-sections")
@RequiredArgsConstructor
public class AdvisorClassSectionController {

    private final AdvisorClassSectionService advisorService;

    // 1. Gán cố vấn cho lớp
    @PostMapping
    public ResponseEntity<AdvisorClassSectionResponseDTO> assignAdvisorToClass(
            @Valid @RequestBody AdvisorClassSectionRequestDTO requestDTO) {
        return new ResponseEntity<>(advisorService.assignAdvisorToClass(requestDTO), HttpStatus.CREATED);
    }

    // 2. Lấy danh sách tất cả
    @GetMapping
    public ResponseEntity<List<AdvisorClassSectionResponseDTO>> getAll() {
        return ResponseEntity.ok(advisorService.getAll());
    }

    // 3. Lấy các lớp cố vấn phụ trách
    @GetMapping("/advisor/{advisorId}")
    public ResponseEntity<List<AdvisorClassSectionResponseDTO>> getByAdvisorId(@PathVariable UUID advisorId) {
        return ResponseEntity.ok(advisorService.getByAdvisorId(advisorId));
    }

    // 4. Lấy cố vấn của lớp
    @GetMapping("/class/{studentClassesId}")
    public ResponseEntity<List<AdvisorClassSectionResponseDTO>> getByClassId(@PathVariable UUID studentClassesId) {
        return ResponseEntity.ok(advisorService.getByClassId(studentClassesId));
    }

    // 5. Cập nhật thông tin cố vấn
    @PutMapping("/{id}")
    public ResponseEntity<AdvisorClassSectionResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody AdvisorClassSectionRequestDTO requestDTO) {
        return ResponseEntity.ok(advisorService.update(id, requestDTO));
    }

    // 6. Xóa mềm
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        advisorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}