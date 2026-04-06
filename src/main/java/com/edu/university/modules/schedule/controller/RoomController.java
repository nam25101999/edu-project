package com.edu.university.modules.schedule.controller;

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
    public ResponseEntity<RoomResponseDTO> create(@Valid @RequestBody RoomRequestDTO requestDTO) {
        return new ResponseEntity<>(roomService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(roomService.getAll(pageable).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(roomService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody RoomRequestDTO requestDTO) {
        return ResponseEntity.ok(roomService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
