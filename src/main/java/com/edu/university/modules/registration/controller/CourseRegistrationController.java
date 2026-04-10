package com.edu.university.modules.registration.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.registration.dto.request.CourseRegistrationRequestDTO;
import com.edu.university.modules.registration.dto.request.EligibilityCheckRequest;
import com.edu.university.modules.registration.dto.response.CourseRegistrationResponseDTO;
import com.edu.university.modules.registration.dto.response.EligibilityCheckResponse;
import com.edu.university.modules.registration.service.CourseRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/course-registrations")
@RequiredArgsConstructor
public class CourseRegistrationController {

    private final CourseRegistrationService courseRegistrationService;

    @PostMapping
    public ResponseEntity<BaseResponse<CourseRegistrationResponseDTO>> create(@Valid @RequestBody CourseRegistrationRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.created("Đăng ký môn học thành công", courseRegistrationService.create(requestDTO)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<CourseRegistrationResponseDTO>>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(courseRegistrationService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CourseRegistrationResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(courseRegistrationService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<CourseRegistrationResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody CourseRegistrationRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật đăng ký thành công", courseRegistrationService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        courseRegistrationService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa đăng ký thành công", null));
    }

    @PostMapping("/check-eligibility")
    public ResponseEntity<BaseResponse<EligibilityCheckResponse>> checkEligibility(@RequestBody EligibilityCheckRequest request) {
        return ResponseEntity.ok(BaseResponse.ok(courseRegistrationService.checkEligibility(request)));
    }
}
