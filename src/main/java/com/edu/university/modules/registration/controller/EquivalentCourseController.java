package com.edu.university.modules.registration.controller;
 
import com.edu.university.modules.registration.dto.request.EquivalentCourseRequestDTO;
import com.edu.university.modules.registration.dto.response.EquivalentCourseResponseDTO;
import com.edu.university.modules.registration.service.EquivalentCourseService;
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
@RequestMapping("/api/equivalent-courses")
@RequiredArgsConstructor
public class EquivalentCourseController {
 
    private final EquivalentCourseService equivalentCourseService;
 
    @PostMapping
    public ResponseEntity<EquivalentCourseResponseDTO> create(@Valid @RequestBody EquivalentCourseRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(equivalentCourseService.create(requestDTO));
    }
 
    @GetMapping
    public ResponseEntity<List<EquivalentCourseResponseDTO>> getAll(@PageableDefault(size = 100) Pageable pageable) {
        return ResponseEntity.ok(equivalentCourseService.getAll(pageable).getContent());
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<EquivalentCourseResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(equivalentCourseService.getById(id));
    }
 
    @PutMapping("/{id}")
    public ResponseEntity<EquivalentCourseResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody EquivalentCourseRequestDTO requestDTO) {
        return ResponseEntity.ok(equivalentCourseService.update(id, requestDTO));
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        equivalentCourseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
