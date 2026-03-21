package com.edu.university.modules.finance.repository;

import com.edu.university.modules.finance.entity.TuitionFee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TuitionFeeRepository extends JpaRepository<TuitionFee, UUID> {
    Optional<TuitionFee> findByStudentIdAndSemesterAndYear(UUID studentId, String semester, Integer year);

    // THÊM HÀM NÀY ĐỂ FIX LỖI Ở ENROLLMENT SERVICE
    List<TuitionFee> findByStudentId(UUID studentId);
}