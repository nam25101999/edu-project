package com.edu.university.modules.finance.seeder;

import com.edu.university.common.seeder.ModuleSeeder;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.finance.entity.Payment;
import com.edu.university.modules.finance.entity.StudentTuition;
import com.edu.university.modules.finance.entity.TuitionFee;
import com.edu.university.modules.finance.repository.PaymentRepository;
import com.edu.university.modules.finance.repository.StudentTuitionRepository;
import com.edu.university.modules.finance.repository.TuitionFeeRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinanceDataSeeder implements ModuleSeeder {

    private final TuitionFeeRepository tuitionFeeRepository;
    private final StudentTuitionRepository studentTuitionRepository;
    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final SemesterRepository semesterRepository;

    @Override
    public void seed() {
        log.info("Seeding Finance data...");
        if (tuitionFeeRepository.count() > 0) return;

        List<TrainingProgram> programs = trainingProgramRepository.findAll();
        List<Student> students = studentRepository.findAll();
        List<Semester> semesters = semesterRepository.findAll();

        if (programs.isEmpty() || students.isEmpty() || semesters.isEmpty()) {
            log.warn("Cannot seed finance: Missing dependencies (Programs, Students, or Semesters).");
            return;
        }

        List<TuitionFee> fees = seedTuitionFees(programs);
        List<StudentTuition> tuitions = seedStudentTuitions(students, semesters, fees);
        seedPayments(tuitions);
    }

    @Override
    public int getOrder() {
        return 70;
    }

    private List<TuitionFee> seedTuitionFees(List<TrainingProgram> programs) {
        List<TuitionFee> fees = new ArrayList<>();
        for (int i = 0; i < programs.size(); i++) {
            fees.add(TuitionFee.builder()
                    .trainingProgram(programs.get(i))
                    .courseYear("2023")
                    .pricePerCredit(BigDecimal.valueOf(450000 + (i * 50000)))
                    .baseTuition(BigDecimal.valueOf(15000000 + (i * 2000000)))
                    .isActive(true)
                    .build());
        }
        return tuitionFeeRepository.saveAll(fees);
    }

    private List<StudentTuition> seedStudentTuitions(List<Student> students, List<Semester> semesters, List<TuitionFee> fees) {
        if (studentTuitionRepository.count() > 0) return studentTuitionRepository.findAll();
        List<StudentTuition> tuitions = new ArrayList<>();
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            TuitionFee fee = fees.get(i % fees.size());
            BigDecimal raw = fee.getBaseTuition().add(fee.getPricePerCredit().multiply(BigDecimal.valueOf(15)));
            BigDecimal paid = i % 3 == 0 ? raw : (i % 3 == 1 ? raw.multiply(new BigDecimal("0.5")) : BigDecimal.ZERO);

            tuitions.add(StudentTuition.builder()
                    .student(s)
                    .semester(semesters.get(i % semesters.size()))
                    .tuitionFee(fee)
                    .totalCredits(15)
                    .rawAmount(raw)
                    .scholarshipDeduction(BigDecimal.ZERO)
                    .exemptionAmount(BigDecimal.ZERO)
                    .netAmount(raw)
                    .paidAmount(paid)
                    .debtAmount(raw.subtract(paid))
                    .status(paid.compareTo(raw) >= 0 ? 1 : (paid.compareTo(BigDecimal.ZERO) > 0 ? 2 : 3))
                    .deadline(LocalDate.now().plusMonths(1))
                    .isActive(true)
                    .build());
        }
        return studentTuitionRepository.saveAll(tuitions);
    }

    private void seedPayments(List<StudentTuition> tuitions) {
        if (paymentRepository.count() > 0) return;
        List<Payment> payments = new ArrayList<>();
        for (StudentTuition tu : tuitions) {
            if (tu.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
                payments.add(Payment.builder()
                        .studentTuition(tu)
                        .amountPaid(tu.getPaidAmount())
                        .paymentDate(LocalDateTime.now())
                        .paymentMethod(1)
                        .transactionRef("TXN_" + tu.getId().toString().substring(0, 8).toUpperCase())
                        .paymentStatus("SUCCESS")
                        .notes("Đóng học phí kỳ " + tu.getSemester().getSemesterName())
                        .isActive(true)
                        .build());
            }
        }
        paymentRepository.saveAll(payments);
    }
}
