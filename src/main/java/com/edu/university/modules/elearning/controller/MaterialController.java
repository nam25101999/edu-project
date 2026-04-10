package com.edu.university.modules.elearning.controller;
 
import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.elearning.dto.response.MaterialResponseDTO;
import com.edu.university.modules.elearning.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
 
import java.util.List;
import java.util.UUID;
 
@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {
 
    private final MaterialService materialService;
 
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MaterialResponseDTO>> createMaterial(
            @RequestParam UUID courseSectionId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) MultipartFile file
    ) {
        // Simple logic to match service signature
        String fileUrl = file != null ? "uploads/" + file.getOriginalFilename() : null;
        Long fileSize = file != null ? file.getSize() : null;
        
        MaterialResponseDTO material = materialService.createMaterial(courseSectionId, title, description, fileUrl, fileType, fileSize);
        return ResponseEntity.ok(ApiResponse.success("Tạo tài liệu thành công", material));
    }
 
    @GetMapping("/course-section/{id}")
    public ResponseEntity<ApiResponse<List<MaterialResponseDTO>>> getMaterialsByCourseSection(
            @PathVariable UUID id,
            Pageable pageable) {
        List<MaterialResponseDTO> list = materialService.getMaterialsByCourseSection(id, pageable).getContent();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
