package com.roomkh.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardResponseDTO {

    // Users & Segments
    private long totalUsers;
    private long activeUsers;
    private long suspendedOrBlockedUsers;
    private long activeRenters;
    private long activeLandlords;
    private long activeStudents;
    private long activeWorkers;

    // Rooms & Occupancy
    private long totalRooms;
    private long pendingModerationRooms;
    private long approvedVacantRooms;
    private long approvedOccupiedRooms;

    // Tenancy & Financials
    private long pendingBookings;
    private long activeContracts;
    private BigDecimal totalPaidRevenue;
    private BigDecimal totalUnpaidAmount;
    private long unpaidBillsCount;

    // Admin Action Queues
    private long pendingLandlordRequests;
    private long pendingRoomReports;
    private long pendingDisputeReports;
    private long pendingSupportRequests;
}
