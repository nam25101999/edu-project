package com.edu.university.modules.studentservice.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.studentservice.dto.request.ConductScoreRequest;
import com.edu.university.modules.studentservice.entity.ConductScore;
import com.edu.university.modules.studentservice.service.ConductScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/conduct-scores")
@RequiredArgsConstructor
public class ConductScoreController {

    private final ConductScoreService conductScoreService;

    @PutMapping("/update")
    public ResponseEntity<BaseResponse<ConductScore>> updateConductScore(@RequestBody ConductScoreRequest request) {
        ConductScore conductScore = conductScoreService.updateConductScore(
                request.getStudentId(),
                request.getSemesterId(),
                request.getScore()
        );
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật điểm rèn luyện thành công", conductScore));
    }
}
