package com.edu.university.modules.graduation.controller;
 
import com.edu.university.modules.graduation.dto.request.GraduationConditionRequestDTO;
import com.edu.university.modules.graduation.dto.response.GraduationConditionResponseDTO;
import com.edu.university.modules.graduation.service.GraduationConditionService;
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
@RequestMapping("/api/graduation-conditions")
@RequiredArgsConstructor
public class GraduationConditionController {
 
    private final GraduationConditionService graduationConditionService;
 
    @PostMapping
    public ResponseEntity<GraduationConditionResponseDTO> create(@Valid @RequestBody GraduationConditionRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(graduationConditionService.create(requestDTO));
    }
 
    @GetMapping
    public ResponseEntity<List<GraduationConditionResponseDTO>> getAll(@PageableDefault(size = 100) Pageable pageable) {
        return ResponseEntity.ok(graduationConditionService.getAll(pageable).getContent());
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<GraduationConditionResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(graduationConditionService.getById(id));
    }
 
    @PutMapping("/{id}")
    public ResponseEntity<GraduationConditionResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody GraduationConditionRequestDTO requestDTO) {
        return ResponseEntity.ok(graduationConditionService.update(id, requestDTO));
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        graduationConditionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
