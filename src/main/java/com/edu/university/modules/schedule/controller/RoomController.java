package com.edu.university.modules.schedule.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.schedule.dto.request.RoomRequestDTO;
import com.edu.university.modules.schedule.dto.response.RoomResponseDTO;
import com.edu.university.modules.schedule.service.RoomService;
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
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<BaseResponse<RoomResponseDTO>> create(@Valid @RequestBody RoomRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.created(roomService.create(requestDTO)));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(roomService.getAll(pageable).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<RoomResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(roomService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<RoomResponseDTO>> update(@PathVariable UUID id,
            @Valid @RequestBody RoomRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok(roomService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
