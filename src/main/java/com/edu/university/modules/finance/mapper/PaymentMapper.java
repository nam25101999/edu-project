package com.edu.university.modules.finance.mapper;

import com.edu.university.modules.finance.dto.request.PaymentRequestDTO;
import com.edu.university.modules.finance.dto.response.PaymentResponseDTO;
import com.edu.university.modules.finance.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {
    @Mapping(target = "studentTuition", ignore = true)
    @Mapping(target = "cashier", ignore = true)
    Payment toEntity(PaymentRequestDTO requestDTO);

    @Mapping(target = "studentTuitionId", source = "studentTuition.id")
    @Mapping(target = "cashierId", source = "cashier.id")
    @Mapping(target = "cashierUsername", source = "cashier.username")
    PaymentResponseDTO toResponseDTO(Payment payment);

    @Mapping(target = "studentTuition", ignore = true)
    @Mapping(target = "cashier", ignore = true)
    void updateEntityFromDTO(PaymentRequestDTO requestDTO, @MappingTarget Payment payment);
}
