package com.edu.university.modules.schedule.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.schedule.dto.request.BuildingRequestDTO;
import com.edu.university.modules.schedule.dto.response.BuildingResponseDTO;
import com.edu.university.modules.schedule.service.BuildingService;
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
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @PostMapping
    public ResponseEntity<BaseResponse<BuildingResponseDTO>> create(@Valid @RequestBody BuildingRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.created(buildingService.create(requestDTO)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<BuildingResponseDTO>>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.ok(buildingService.getAll(pageable).getContent()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<BuildingResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(buildingService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<BuildingResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody BuildingRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok(buildingService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        buildingService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok());
    }
}
