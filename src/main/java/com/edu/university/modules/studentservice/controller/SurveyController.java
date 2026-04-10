package com.edu.university.modules.studentservice.controller;
 
import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.studentservice.dto.request.SurveyResponseRequest;
import com.edu.university.modules.studentservice.dto.response.SurveyResponseDTO;
import com.edu.university.modules.studentservice.dto.response.SurveyResultResponseDTO;
import com.edu.university.modules.studentservice.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.UUID;
 
@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController {
 
    private final SurveyService surveyService;
 
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SurveyResponseDTO>>> getActiveSurveys(
            Pageable pageable) {
        List<SurveyResponseDTO> list = surveyService.getActiveSurveys(pageable).getContent();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khảo sát thành công", list));
    }
 
    @PostMapping("/{id}/respond")
    public ResponseEntity<ApiResponse<SurveyResultResponseDTO>> submitResponse(
            @PathVariable UUID id,
            @RequestBody SurveyResponseRequest request
    ) {
        SurveyResultResponseDTO result = surveyService.submitResponse(id, request.getStudentId(), request.getAnswersJson());
        return ResponseEntity.ok(ApiResponse.success("Gửi câu trả lời khảo sát thành công", result));
    }
}
