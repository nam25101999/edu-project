package com.edu.university.modules.studentservice.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.studentservice.dto.request.SurveyResponseRequest;
import com.edu.university.modules.studentservice.entity.Survey;
import com.edu.university.modules.studentservice.entity.SurveyResponse;
import com.edu.university.modules.studentservice.service.SurveyService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<BaseResponse<List<Survey>>> getActiveSurveys() {
        return ResponseEntity.ok(BaseResponse.ok(surveyService.getActiveSurveys()));
    }

    @PostMapping("/{id}/respond")
    public ResponseEntity<BaseResponse<SurveyResponse>> submitResponse(
            @PathVariable UUID id,
            @RequestBody SurveyResponseRequest request
    ) {
        SurveyResponse response = surveyService.submitResponse(id, request.getStudentId(), request.getAnswersJson());
        return ResponseEntity.ok(BaseResponse.ok("Gửi câu trả lời khảo sát thành công", response));
    }
}
