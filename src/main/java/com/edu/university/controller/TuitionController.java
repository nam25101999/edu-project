package com.edu.university.controller;

import com.edu.university.entity.PaymentHistory;
import com.edu.university.entity.TuitionFee;
import com.edu.university.service.TuitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/tuition")
@RequiredArgsConstructor
public class TuitionController {

    private final TuitionService tuitionService;

    @PostMapping("/calculate")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<?> calculateTuition(
            @RequestParam UUID studentId,
            @RequestParam String semester,
            @RequestParam Integer year) {
        return ResponseEntity.ok(tuitionService.calculateTuition(studentId, semester, year));
    }

    @PostMapping("/{tuitionFeeId}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> makePayment(
            @PathVariable UUID tuitionFeeId,
            @RequestBody Map<String, Object> paymentData) {

        Double amount = Double.valueOf(paymentData.get("amount").toString());
        String method = paymentData.getOrDefault("method", "TIEN_MAT").toString();
        String note = paymentData.getOrDefault("note", "").toString();

        return ResponseEntity.ok(tuitionService.makePayment(tuitionFeeId, amount, method, note));
    }

    @GetMapping("/{tuitionFeeId}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<?> getPaymentHistory(@PathVariable UUID tuitionFeeId) {
        return ResponseEntity.ok(tuitionService.getPaymentHistory(tuitionFeeId));
    }

    @GetMapping("/{tuitionFeeId}/invoice/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<String> exportInvoice(@PathVariable UUID tuitionFeeId) {
        // Trả về một chuỗi hóa đơn dạng text/csv (Trong thực tế có thể dùng Apache POI/iTextPDF)
        List<PaymentHistory> history = tuitionService.getPaymentHistory(tuitionFeeId);
        StringBuilder invoice = new StringBuilder();
        invoice.append("===== HÓA ĐƠN HỌC PHÍ =====\n");
        invoice.append("Mã Học Phí: ").append(tuitionFeeId).append("\n");
        history.forEach(h -> invoice.append("Ngày: ").append(h.getPaymentDate())
                .append(" | Số tiền: ").append(h.getAmountPaid())
                .append(" | Phương thức: ").append(h.getPaymentMethod()).append("\n"));
        invoice.append("===========================\n");

        return ResponseEntity.ok(invoice.toString());
    }
}