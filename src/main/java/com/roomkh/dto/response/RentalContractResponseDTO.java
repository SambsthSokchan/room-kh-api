package com.roomkh.dto.response;

import com.roomkh.enums.ContractStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
public class RentalContractResponseDTO {
    private Long contractId;
    private Long bookingId;
    private Long userId;
    private String renterName;
    private Long roomId;
    private String roomTitle;
    private Long landlordId;
    private String businessName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal monthlyRent;
    private BigDecimal depositAmount;
    private Short paymentDueDay;
    private ContractStatus contractStatus;
    private String contractTerms;
    private String contractPdfUrl;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
