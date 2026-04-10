package com.edu.university.modules.system.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.system.dto.request.NotificationRequestDTO;
import com.edu.university.modules.system.dto.response.NotificationResponseDTO;
import com.edu.university.modules.system.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<BaseResponse<NotificationResponseDTO>> create(@Valid @RequestBody NotificationRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.created(notificationService.create(requestDTO)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<NotificationResponseDTO>>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(notificationService.getAll(pageable)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        notificationService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok());
    }
}
