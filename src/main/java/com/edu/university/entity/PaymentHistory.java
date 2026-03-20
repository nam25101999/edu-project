package com.edu.university.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_histories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tuition_fee_id", nullable = false)
    private TuitionFee tuitionFee;

    @Column(nullable = false)
    private Double amountPaid;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    private String paymentMethod; // VD: "CHUYEN_KHOAN", "TIEN_MAT"

    private String note;
}