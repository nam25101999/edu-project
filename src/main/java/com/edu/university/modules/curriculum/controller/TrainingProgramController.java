package com.edu.university.modules.curriculum.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.curriculum.dto.request.TrainingProgramRequestDTO;
import com.edu.university.modules.curriculum.dto.response.TrainingProgramResponseDTO;
import com.edu.university.modules.curriculum.service.TrainingProgramService;
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
@RequestMapping("/api/training-programs")
@RequiredArgsConstructor
public class TrainingProgramController {

    private final TrainingProgramService trainingProgramService;

    @PostMapping
    public ResponseEntity<BaseResponse<TrainingProgramResponseDTO>> create(@Valid @RequestBody TrainingProgramRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Tạo chương trình đào tạo thành công", trainingProgramService.create(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<TrainingProgramResponseDTO>>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.ok(trainingProgramService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<TrainingProgramResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(trainingProgramService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<TrainingProgramResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody TrainingProgramRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật chương trình đào tạo thành công", trainingProgramService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        trainingProgramService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa chương trình đào tạo thành công", null));
    }
}
