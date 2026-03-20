package com.edu.university.service;

import com.edu.university.entity.*;
import com.edu.university.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TuitionService {

    private final TuitionFeeRepository tuitionFeeRepo;
    private final PaymentHistoryRepository paymentRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final StudentRepository studentRepo;

    // Đơn giá 1 tín chỉ (Ví dụ: 500.000 VNĐ)
    private static final Double FEE_PER_CREDIT = 500000.0;

    @Transactional
    public TuitionFee calculateTuition(UUID studentId, String semester, Integer year) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Lấy tất cả môn đã đăng ký trong học kỳ
        List<Enrollment> enrollments = enrollmentRepo.findByStudentId(studentId).stream()
                .filter(e -> e.getClassSection().getSemester().equals(semester) &&
                        e.getClassSection().getYear().equals(year))
                .toList();

        int totalCredits = enrollments.stream()
                .mapToInt(e -> e.getClassSection().getCourse().getCredits())
                .sum();

        double totalAmount = totalCredits * FEE_PER_CREDIT;

        // Tìm hoặc tạo mới bản ghi học phí
        TuitionFee fee = tuitionFeeRepo.findByStudentIdAndSemesterAndYear(studentId, semester, year)
                .orElse(TuitionFee.builder()
                        .student(student)
                        .semester(semester)
                        .year(year)
                        .paidAmount(0.0)
                        .status(TuitionStatus.CHUA_DONG)
                        .build());

        fee.setTotalCredits(totalCredits);
        fee.setTotalAmount(totalAmount);

        // Cập nhật lại trạng thái dựa trên số tiền đã đóng
        updateStatus(fee);

        return tuitionFeeRepo.save(fee);
    }

    @Transactional
    public PaymentHistory makePayment(UUID tuitionFeeId, Double amount, String method, String note) {
        TuitionFee fee = tuitionFeeRepo.findById(tuitionFeeId)
                .orElseThrow(() -> new RuntimeException("Tuition fee record not found"));

        if (fee.getStatus() == TuitionStatus.DA_DONG) {
            throw new RuntimeException("Học phí kỳ này đã được đóng đủ.");
        }

        fee.setPaidAmount(fee.getPaidAmount() + amount);
        updateStatus(fee);
        tuitionFeeRepo.save(fee);

        PaymentHistory payment = PaymentHistory.builder()
                .tuitionFee(fee)
                .amountPaid(amount)
                .paymentDate(LocalDateTime.now())
                .paymentMethod(method)
                .note(note)
                .build();

        return paymentRepo.save(payment);
    }

    public List<PaymentHistory> getPaymentHistory(UUID tuitionFeeId) {
        return paymentRepo.findByTuitionFeeId(tuitionFeeId);
    }

    private void updateStatus(TuitionFee fee) {
        if (fee.getPaidAmount() >= fee.getTotalAmount()) {
            fee.setStatus(TuitionStatus.DA_DONG);
            fee.setPaidAmount(fee.getTotalAmount()); // Không cho vượt quá
        } else if (fee.getPaidAmount() > 0) {
            fee.setStatus(TuitionStatus.DONG_MOT_PHAN);
        } else {
            fee.setStatus(TuitionStatus.CHUA_DONG);
        }
    }
}