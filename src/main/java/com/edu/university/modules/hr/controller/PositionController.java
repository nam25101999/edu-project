package com.edu.university.modules.hr.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.hr.dto.request.PositionRequestDTO;
import com.edu.university.modules.hr.dto.response.PositionResponseDTO;
import com.edu.university.modules.hr.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @PostMapping
    public ResponseEntity<BaseResponse<PositionResponseDTO>> createPosition(@Valid @RequestBody PositionRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Tạo chức vụ thành công", positionService.createPosition(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<PositionResponseDTO>>> getAllPositions(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(positionService.getAllPositions(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PositionResponseDTO>> getPositionById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(positionService.getPositionById(id)));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<BaseResponse<PositionResponseDTO>> getPositionByCode(@PathVariable String code) {
        return ResponseEntity.ok(BaseResponse.ok(positionService.getPositionByCode(code)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<PositionResponseDTO>> updatePosition(
            @PathVariable UUID id,
            @Valid @RequestBody PositionRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật chức vụ thành công", positionService.updatePosition(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deletePosition(@PathVariable UUID id) {
        positionService.deletePosition(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa chức vụ thành công", null));
    }
}
