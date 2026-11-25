package com.roomkh.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserDashboardResponseDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private String userSegment;

    // Tenancy & Bookings
    private long activeContracts;
    private long pendingBookings;
    private long totalBookings;

    // Payments & Dues
    private long unpaidBillsCount;
    private BigDecimal totalDueAmount;
    private BigDecimal totalPaidAmount;

    // Activity & Engagement
    private long unreadNotificationsCount;
    private long submittedRatingsCount;
    private long submittedSupportRequestsCount;
}
