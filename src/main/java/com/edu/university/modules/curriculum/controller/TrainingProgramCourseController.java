package com.edu.university.modules.curriculum.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.curriculum.dto.request.TrainingProgramCourseRequestDTO;
import com.edu.university.modules.curriculum.dto.response.TrainingProgramCourseResponseDTO;
import com.edu.university.modules.curriculum.service.TrainingProgramCourseService;
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
@RequestMapping("/api/training-program-courses")
@RequiredArgsConstructor
public class TrainingProgramCourseController {

    private final TrainingProgramCourseService trainingProgramCourseService;

    @PostMapping
    public ResponseEntity<BaseResponse<TrainingProgramCourseResponseDTO>> create(@Valid @RequestBody TrainingProgramCourseRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Thêm môn học vào chương trình đào tạo thành công", trainingProgramCourseService.create(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<TrainingProgramCourseResponseDTO>>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.ok(trainingProgramCourseService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<TrainingProgramCourseResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(trainingProgramCourseService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<TrainingProgramCourseResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody TrainingProgramCourseRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật môn học trong chương trình đào tạo thành công", trainingProgramCourseService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        trainingProgramCourseService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa môn học khỏi chương trình đào tạo thành công", null));
    }
}
