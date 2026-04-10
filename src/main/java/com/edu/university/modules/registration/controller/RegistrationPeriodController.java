package com.edu.university.modules.registration.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.registration.dto.request.RegistrationPeriodRequestDTO;
import com.edu.university.modules.registration.dto.response.RegistrationPeriodResponseDTO;
import com.edu.university.modules.registration.service.RegistrationPeriodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/registration-periods")
@RequiredArgsConstructor
public class RegistrationPeriodController {

    private final RegistrationPeriodService registrationPeriodService;

    @PostMapping
    public ResponseEntity<BaseResponse<RegistrationPeriodResponseDTO>> create(@Valid @RequestBody RegistrationPeriodRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.created("Tạo đợt đăng ký thành công", registrationPeriodService.create(requestDTO)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<RegistrationPeriodResponseDTO>>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(registrationPeriodService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<RegistrationPeriodResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(registrationPeriodService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<RegistrationPeriodResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody RegistrationPeriodRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật đợt đăng ký thành công", registrationPeriodService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        registrationPeriodService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa đợt đăng ký thành công", null));
    }
}
