package com.edu.university.modules.system.controller;

import com.edu.university.modules.system.dto.request.SystemSettingRequestDTO;
import com.edu.university.modules.system.dto.response.SystemSettingResponseDTO;
import com.edu.university.modules.system.service.SystemSettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    @PostMapping
    public ResponseEntity<SystemSettingResponseDTO> update(@Valid @RequestBody SystemSettingRequestDTO requestDTO) {
        return ResponseEntity.ok(systemSettingService.update(requestDTO));
    }

    @GetMapping
    public ResponseEntity<List<SystemSettingResponseDTO>> getAll() {
        return ResponseEntity.ok(systemSettingService.getAll());
    }

    @GetMapping("/{key}")
    public ResponseEntity<SystemSettingResponseDTO> getByKey(@PathVariable String key) {
        return ResponseEntity.ok(systemSettingService.getByKey(key));
    }
}
