package com.edu.university.modules.studentservice.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.studentservice.dto.request.PetitionProcessRequest;
import com.edu.university.modules.studentservice.dto.request.PetitionRequest;
import com.edu.university.modules.studentservice.entity.StudentPetition;
import com.edu.university.modules.studentservice.service.StudentPetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/petitions")
@RequiredArgsConstructor
public class StudentPetitionController {

    private final StudentPetitionService petitionService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentPetition>> createPetition(@RequestBody PetitionRequest request) {
        StudentPetition petition = petitionService.createPetition(
                request.getStudentId(),
                request.getTitle(),
                request.getContent(),
                request.getAttachmentUrl()
        );
        return ResponseEntity.ok(BaseResponse.ok("Yêu cầu/Đơn từ đã được gửi", petition));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<BaseResponse<List<StudentPetition>>> getPetitionsByStudent(@PathVariable UUID studentId) {
        List<StudentPetition> petitions = petitionService.getPetitionsByStudent(studentId);
        return ResponseEntity.ok(BaseResponse.ok(petitions));
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<BaseResponse<StudentPetition>> processPetition(
            @PathVariable UUID id,
            @RequestBody PetitionProcessRequest request
    ) {
        StudentPetition petition = petitionService.processPetition(id, request.getStatus(), request.getResponseContent());
        return ResponseEntity.ok(BaseResponse.ok("Xử lý đơn từ thành công", petition));
    }
}
