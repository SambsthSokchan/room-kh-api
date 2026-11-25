package com.roomkh.dto.response;

import com.roomkh.enums.PaymentMethod;
import com.roomkh.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
public class PaymentResponseDTO {
    private Long paymentId;
    private Long contractId;
    private Long userId;
    private String renterName;
    private Long landlordId;
    private String businessName;
    private Long roomId;
    private String roomTitle;
    private LocalDate paymentMonth;
    private LocalDate paymentDate;
    private BigDecimal baseRentAmount;
    private BigDecimal waterFee;
    private BigDecimal electricityFee;
    private BigDecimal otherFees;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private String receiptUrl;
    private String paymentNote;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
