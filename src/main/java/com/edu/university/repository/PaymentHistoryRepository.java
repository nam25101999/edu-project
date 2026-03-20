package com.edu.university.repository;

import com.edu.university.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, UUID> {
    List<PaymentHistory> findByTuitionFeeId(UUID tuitionFeeId);
}