package com.edu.university.modules.finance.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.finance.dto.request.PaymentRequestDTO;
import com.edu.university.modules.finance.dto.response.PaymentResponseDTO;
import com.edu.university.modules.finance.service.PaymentService;
import com.edu.university.modules.finance.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final VnPayService vnPayService;

    @PostMapping
    public ResponseEntity<BaseResponse<PaymentResponseDTO>> create(@Valid @RequestBody PaymentRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Tạo thanh toán thành công", paymentService.create(requestDTO)), 
                HttpStatus.CREATED
        );
    }

    @GetMapping("/tuition/{studentTuitionId}")
    public ResponseEntity<BaseResponse<PageResponse<PaymentResponseDTO>>> getByStudentTuitionId(
            @PathVariable UUID studentTuitionId,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(paymentService.getByStudentTuitionId(studentTuitionId, pageable)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        paymentService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa thanh toán thành công", null));
    }

    @GetMapping("/vnpay-url")
    public ResponseEntity<BaseResponse<String>> getVnPayUrl(
            @RequestParam long amount,
            @RequestParam String orderInfo,
            HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();
        String paymentUrl = vnPayService.createPaymentUrl(amount, orderInfo, ipAddress);
        return ResponseEntity.ok(BaseResponse.ok(paymentUrl));
    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<BaseResponse<Map<String, String>>> vnPayCallback(@RequestParam Map<String, String> queryParams) {
        String responseCode = queryParams.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            return ResponseEntity.ok(BaseResponse.ok("Thanh toán thành công", queryParams));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.error(400, "Thanh toán thất bại: " + responseCode));
        }
    }
}
