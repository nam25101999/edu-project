package com.edu.university.modules.schedule.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.schedule.dto.request.ScheduleRequestDTO;
import com.edu.university.modules.schedule.dto.response.ScheduleResponseDTO;
import com.edu.university.modules.schedule.service.ScheduleService;
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
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<BaseResponse<ScheduleResponseDTO>> create(@Valid @RequestBody ScheduleRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.created(scheduleService.create(requestDTO)));
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponseDTO>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(scheduleService.getAll(pageable).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ScheduleResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(scheduleService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ScheduleResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody ScheduleRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok(scheduleService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
