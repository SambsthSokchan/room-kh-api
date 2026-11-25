package com.roomkh.mapper;

import com.roomkh.dto.response.RentalContractResponseDTO;
import com.roomkh.entity.RentalContract;
import org.springframework.stereotype.Component;

@Component
public class RentalContractMapper {

    public RentalContractResponseDTO toResponse(RentalContract contract) {
        return RentalContractResponseDTO.builder()
                .contractId(contract.getContractId())
                .bookingId(contract.getBooking().getBookingId())
                .userId(contract.getUser().getUserId())
                .renterName(contract.getUser().getFullName())
                .roomId(contract.getRoom().getRoomId())
                .roomTitle(contract.getRoom().getRoomTitle())
                .landlordId(contract.getLandlord().getLandlordProfileId())
                .businessName(contract.getLandlord().getBusinessName())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .monthlyRent(contract.getMonthlyRent())
                .depositAmount(contract.getDepositAmount())
                .paymentDueDay(contract.getPaymentDueDay())
                .contractStatus(contract.getContractStatus())
                .contractTerms(contract.getContractTerms())
                .contractPdfUrl(contract.getContractPdfUrl())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .build();
    }
}
