package com.edu.university.modules.system.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.system.dto.request.SystemSettingRequestDTO;
import com.edu.university.modules.system.dto.response.SystemSettingResponseDTO;
import com.edu.university.modules.system.service.SystemSettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    @PostMapping
    public ResponseEntity<BaseResponse<SystemSettingResponseDTO>> update(@Valid @RequestBody SystemSettingRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật cài đặt thành công", systemSettingService.update(requestDTO)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<SystemSettingResponseDTO>>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(systemSettingService.getAll(pageable)));
    }

    @GetMapping("/{key}")
    public ResponseEntity<BaseResponse<SystemSettingResponseDTO>> getByKey(@PathVariable String key) {
        return ResponseEntity.ok(BaseResponse.ok(systemSettingService.getByKey(key)));
    }
}
