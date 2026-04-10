package com.edu.university.modules.schedule.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.schedule.dto.request.TimeSlotRequestDTO;
import com.edu.university.modules.schedule.dto.response.TimeSlotResponseDTO;
import com.edu.university.modules.schedule.service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/time-slots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @PostMapping
    public ResponseEntity<BaseResponse<TimeSlotResponseDTO>> create(@Valid @RequestBody TimeSlotRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.created(timeSlotService.create(requestDTO)));
    }

    @GetMapping
    public ResponseEntity<List<TimeSlotResponseDTO>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(timeSlotService.getAll(pageable).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<TimeSlotResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(timeSlotService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<TimeSlotResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody TimeSlotRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok(timeSlotService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        timeSlotService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
