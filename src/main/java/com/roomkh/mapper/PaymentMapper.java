package com.roomkh.mapper;

import com.roomkh.dto.response.PaymentResponseDTO;
import com.roomkh.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponseDTO toResponse(Payment payment) {
        return PaymentResponseDTO.builder()
                .paymentId(payment.getPaymentId())
                .contractId(payment.getContract().getContractId())
                .userId(payment.getUser().getUserId())
                .renterName(payment.getUser().getFullName())
                .landlordId(payment.getLandlord().getLandlordProfileId())
                .businessName(payment.getLandlord().getBusinessName())
                .roomId(payment.getContract().getRoom().getRoomId())
                .roomTitle(payment.getContract().getRoom().getRoomTitle())
                .paymentMonth(payment.getPaymentMonth())
                .paymentDate(payment.getPaymentDate())
                .baseRentAmount(payment.getBaseRentAmount())
                .waterFee(payment.getWaterFee())
                .electricityFee(payment.getElectricityFee())
                .otherFees(payment.getOtherFees())
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus())
                .paymentMethod(payment.getPaymentMethod())
                .receiptUrl(payment.getReceiptUrl())
                .paymentNote(payment.getPaymentNote())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
