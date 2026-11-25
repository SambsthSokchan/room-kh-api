package com.roomkh.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LandlordDashboardResponseDTO {
    private Long landlordProfileId;
    private String businessName;

    // Room Stats
    private long totalRooms;
    private long vacantRooms;
    private long occupiedRooms;
    private long pendingModerationRooms;
    private long approvedRooms;

    // Tenancy & Requests
    private long pendingBookings;
    private long activeContracts;

    // Financials
    private BigDecimal totalPaidRevenue;
    private BigDecimal totalUnpaidAmount;
    private long unpaidBillsCount;
}
