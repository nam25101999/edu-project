package com.edu.university.modules.system.controller;

import com.edu.university.modules.system.dto.response.SystemLogResponseDTO;
import com.edu.university.modules.system.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class SystemLogController {

    private final SystemLogService systemLogService;

    @GetMapping
    public ResponseEntity<List<SystemLogResponseDTO>> getAll() {
        return ResponseEntity.ok(systemLogService.getAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SystemLogResponseDTO>> getByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(systemLogService.getByUserId(userId));
    }
}
