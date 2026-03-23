package com.edu.university.modules.finance.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.enrollment.entity.Enrollment;
import com.edu.university.modules.enrollment.repository.EnrollmentRepository;
import com.edu.university.modules.finance.entity.PaymentHistory;
import com.edu.university.modules.finance.entity.TuitionFee;
import com.edu.university.modules.finance.entity.TuitionStatus;
import com.edu.university.modules.finance.repository.PaymentHistoryRepository;
import com.edu.university.modules.finance.repository.TuitionFeeRepository;
import com.edu.university.modules.report.annotation.LogAction;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service quản lý học phí và thanh toán.
 * Đã chuẩn hóa lỗi theo Enterprise Standard (ErrorCode FNC, STD).
 */
@Service
@RequiredArgsConstructor
public class TuitionService {

    private final TuitionFeeRepository tuitionFeeRepo;
    private final PaymentHistoryRepository paymentRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final StudentRepository studentRepo;

    private static final Double FEE_PER_CREDIT = 500000.0;

    @LogAction(action = "CALCULATE_TUITION", entityName = "TUITION_FEE")
    @Transactional
    public TuitionFee calculateTuition(UUID studentId, String semester, Integer year) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

        List<Enrollment> enrollments = enrollmentRepo.findByStudentId(studentId).stream()
                .filter(e -> e.getClassSection().getSemester().equals(semester) &&
                        e.getClassSection().getYear().equals(year))
                .toList();

        int totalCredits = enrollments.stream()
                .mapToInt(e -> e.getClassSection().getCourse().getCredits())
                .sum();

        double totalAmount = totalCredits * FEE_PER_CREDIT;

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

        updateStatus(fee);

        return tuitionFeeRepo.save(fee);
    }

    @LogAction(action = "MAKE_PAYMENT", entityName = "PAYMENT_HISTORY")
    @Transactional
    public PaymentHistory makePayment(UUID tuitionFeeId, Double amount, String method, String note) {
        TuitionFee fee = tuitionFeeRepo.findById(tuitionFeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TUITION_RECORD_NOT_FOUND));

        if (fee.getStatus() == TuitionStatus.DA_DONG) {
            throw new BusinessException(ErrorCode.TUITION_ALREADY_PAID);
        }

        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_AMOUNT);
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
        if (!tuitionFeeRepo.existsById(tuitionFeeId)) {
            throw new BusinessException(ErrorCode.TUITION_RECORD_NOT_FOUND);
        }
        return paymentRepo.findByTuitionFeeId(tuitionFeeId);
    }

    private void updateStatus(TuitionFee fee) {
        if (fee.getPaidAmount() >= fee.getTotalAmount()) {
            fee.setStatus(TuitionStatus.DA_DONG);
            fee.setPaidAmount(fee.getTotalAmount());
        } else if (fee.getPaidAmount() > 0) {
            fee.setStatus(TuitionStatus.DONG_MOT_PHAN);
        } else {
            fee.setStatus(TuitionStatus.CHUA_DONG);
        }
    }
}