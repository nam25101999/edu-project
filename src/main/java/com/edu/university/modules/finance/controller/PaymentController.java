package com.edu.university.modules.finance.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.finance.dto.request.PaymentRequestDTO;
import com.edu.university.modules.finance.dto.response.PaymentResponseDTO;
import com.edu.university.modules.finance.service.PaymentService;
import com.edu.university.modules.finance.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final VnPayService vnPayService;

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

    @GetMapping("/vnpay-url")
    public ResponseEntity<ApiResponse<String>> getVnPayUrl(
            @RequestParam long amount,
            @RequestParam String orderInfo,
            HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();
        String paymentUrl = vnPayService.createPaymentUrl(amount, orderInfo, ipAddress);
        return ResponseEntity.ok(ApiResponse.success(paymentUrl));
    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<ApiResponse<Map<String, String>>> vnPayCallback(@RequestParam Map<String, String> queryParams) {
        String responseCode = queryParams.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            return ResponseEntity.ok(ApiResponse.success("Thanh toán thành công", queryParams));
        } else {
            return ResponseEntity.ok(new ApiResponse<>(400, "FIN_002", "Thanh toán thất bại: " + responseCode, null, null, null, null, LocalDateTime.now()));
        }
    }
}
