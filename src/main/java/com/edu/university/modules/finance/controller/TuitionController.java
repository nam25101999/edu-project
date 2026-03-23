package com.edu.university.modules.finance.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.finance.entity.PaymentHistory;
import com.edu.university.modules.finance.entity.TuitionFee;
import com.edu.university.modules.finance.service.TuitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.Map;
import java.util.List;

/**
 * Controller xử lý tài chính và học phí.
 * Trả về kết quả qua ApiResponse chuẩn hóa.
 */
@RestController
@RequestMapping("/api/tuition")
@RequiredArgsConstructor
public class TuitionController {

    private final TuitionService tuitionService;

    @PostMapping("/calculate")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ApiResponse<TuitionFee> calculateTuition(
            @RequestParam UUID studentId,
            @RequestParam String semester,
            @RequestParam Integer year) {
        return ApiResponse.success("Tính toán học phí thành công",
                tuitionService.calculateTuition(studentId, semester, year));
    }

    @PostMapping("/{tuitionFeeId}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PaymentHistory> makePayment(
            @PathVariable UUID tuitionFeeId,
            @RequestBody Map<String, Object> paymentData) {

        Double amount = Double.valueOf(paymentData.getOrDefault("amount", 0).toString());
        String method = paymentData.getOrDefault("method", "TIEN_MAT").toString();
        String note = paymentData.getOrDefault("note", "").toString();

        return ApiResponse.success("Thanh toán học phí thành công",
                tuitionService.makePayment(tuitionFeeId, amount, method, note));
    }

    @GetMapping("/{tuitionFeeId}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ApiResponse<List<PaymentHistory>> getPaymentHistory(@PathVariable UUID tuitionFeeId) {
        return ApiResponse.success(tuitionService.getPaymentHistory(tuitionFeeId));
    }

    @GetMapping("/{tuitionFeeId}/invoice/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ApiResponse<String> exportInvoice(@PathVariable UUID tuitionFeeId) {
        List<PaymentHistory> history = tuitionService.getPaymentHistory(tuitionFeeId);

        StringBuilder invoice = new StringBuilder();
        invoice.append("===== HÓA ĐƠN HỌC PHÍ =====\n");
        invoice.append("Mã Học Phí: ").append(tuitionFeeId).append("\n");
        history.forEach(h -> invoice.append("Ngày: ").append(h.getPaymentDate())
                .append(" | Số tiền: ").append(h.getAmountPaid())
                .append(" | Phương thức: ").append(h.getPaymentMethod()).append("\n"));
        invoice.append("===========================\n");

        return ApiResponse.success("Xuất hóa đơn thành công", invoice.toString());
    }
}