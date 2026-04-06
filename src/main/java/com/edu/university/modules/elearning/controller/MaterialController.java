package com.edu.university.modules.elearning.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.elearning.entity.Material;
import com.edu.university.modules.elearning.service.MaterialService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ResponseEntity<BaseResponse<Material>> createMaterial(
            @RequestParam UUID courseSectionId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) String externalUrl,
            @RequestParam(required = false) MultipartFile file
    ) {
        // Logic giả lập xử lý file - Trong thực tế sẽ upload lên S3/Local Storage
        String fileUrl = externalUrl != null ? externalUrl : (file != null ? "uploads/" + file.getOriginalFilename() : null);
        Long fileSize = file != null ? file.getSize() : null;
        
        Material material = materialService.createMaterial(courseSectionId, title, description, fileUrl, fileType, fileSize);
        return ResponseEntity.ok(BaseResponse.ok("Upload tài liệu thành công", material));
    }

    @GetMapping("/course-section/{id}")
    public ResponseEntity<BaseResponse<List<Material>>> getMaterialsByCourseSection(@PathVariable UUID id) {
        List<Material> materials = materialService.getMaterialsByCourseSection(id);
        return ResponseEntity.ok(BaseResponse.ok(materials));
    }
}
