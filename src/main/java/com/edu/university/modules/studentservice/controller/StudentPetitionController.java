package com.edu.university.modules.studentservice.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.studentservice.dto.request.PetitionProcessRequest;
import com.edu.university.modules.studentservice.dto.request.PetitionRequest;
import com.edu.university.modules.studentservice.dto.response.StudentPetitionResponseDTO;
import com.edu.university.modules.studentservice.service.StudentPetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/petitions")
@RequiredArgsConstructor
public class StudentPetitionController {

    private final StudentPetitionService petitionService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentPetitionResponseDTO>> createPetition(@RequestBody PetitionRequest request) {
        return ResponseEntity.ok(BaseResponse.ok("Yêu cầu/Đơn từ đã được gửi", petitionService.createPetition(
                request.getStudentId(),
                request.getTitle(),
                request.getContent(),
                request.getAttachmentUrl()
        )));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<BaseResponse<PageResponse<StudentPetitionResponseDTO>>> getPetitionsByStudent(
            @PathVariable UUID studentId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage("Lấy danh sách đơn từ thành công", 
                petitionService.getPetitionsByStudent(studentId, pageable)));
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<BaseResponse<StudentPetitionResponseDTO>> processPetition(
            @PathVariable UUID id,
            @RequestBody PetitionProcessRequest request
    ) {
        return ResponseEntity.ok(BaseResponse.ok("Xử lý đơn từ thành công", 
                petitionService.processPetition(id, request.getStatus(), request.getResponseContent())));
    }
}
