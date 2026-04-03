package com.edu.university.modules.finance.controller;

import com.edu.university.modules.finance.dto.request.PaymentRequestDTO;
import com.edu.university.modules.finance.dto.response.PaymentResponseDTO;
import com.edu.university.modules.finance.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> create(@Valid @RequestBody PaymentRequestDTO requestDTO) {
        return new ResponseEntity<>(paymentService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/tuition/{studentTuitionId}")
    public ResponseEntity<List<PaymentResponseDTO>> getByStudentTuitionId(@PathVariable UUID studentTuitionId) {
        return ResponseEntity.ok(paymentService.getByStudentTuitionId(studentTuitionId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
