package com.edu.university.modules.system.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.system.dto.response.SystemLogResponseDTO;
import com.edu.university.modules.system.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class SystemLogController {

    private final SystemLogService systemLogService;

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<SystemLogResponseDTO>>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(systemLogService.getAll(pageable)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse<PageResponse<SystemLogResponseDTO>>> getByUserId(@PathVariable UUID userId, @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(systemLogService.getByUserId(userId, pageable)));
    }
}
