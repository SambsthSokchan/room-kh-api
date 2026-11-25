package com.roomkh.service.impl;

import com.roomkh.dto.response.DashboardResponseDTO;
import com.roomkh.entity.LandlordProfile;
import com.roomkh.entity.User;
import com.roomkh.enums.*;
import com.roomkh.repository.*;
import com.roomkh.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final LandlordUpgradeRequestRepository upgradeRequestRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final RentalContractRepository contractRepository;
    private final PaymentRepository paymentRepository;
    private final RoomReportRepository roomReportRepository;
    private final DisputeReportRepository disputeReportRepository;
    private final SupportRequestRepository supportRequestRepository;
    private final LandlordProfileRepository landlordProfileRepository;
    private final NotificationRepository notificationRepository;
    private final RoomRatingRepository roomRatingRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponseDTO getAdminDashboard() {
        long suspended = userRepository.countByStatus(UserStatus.SUSPENDED);
        long blocked = userRepository.countByStatus(UserStatus.BLOCKED);

        BigDecimal paidRevenue = paymentRepository.sumAmountByPaymentStatus(PaymentStatus.PAID);
        BigDecimal unpaidAmount = paymentRepository.sumAmountByPaymentStatus(PaymentStatus.UNPAID);

        return DashboardResponseDTO.builder()
                // Users & Segments
                .totalUsers(userRepository.count())
                .activeUsers(userRepository.countByStatus(UserStatus.ACTIVE))
                .suspendedOrBlockedUsers(suspended + blocked)
                .activeRenters(userRepository.countByRoleAndStatus(UserRole.RENTER, UserStatus.ACTIVE))
                .activeLandlords(userRepository.countByRoleAndStatus(UserRole.LANDLORD, UserStatus.ACTIVE))
                .activeStudents(userRepository.countByUserSegmentAndStatus(UserSegment.STUDENT, UserStatus.ACTIVE))
                .activeWorkers(userRepository.countByUserSegmentAndStatus(UserSegment.WORKER, UserStatus.ACTIVE))
                // Rooms & Occupancy
                .totalRooms(roomRepository.count())
                .pendingModerationRooms(roomRepository.countByModerationStatus(ModerationStatus.PENDING))
                .approvedVacantRooms(roomRepository.countByModerationStatusAndRoomStatus(
                        ModerationStatus.APPROVED, RoomStatus.VACANT))
                .approvedOccupiedRooms(roomRepository.countByModerationStatusAndRoomStatus(
                        ModerationStatus.APPROVED, RoomStatus.OCCUPIED))
                // Tenancy & Financials
                .pendingBookings(bookingRepository.countByBookingStatus(BookingStatus.PENDING))
                .activeContracts(contractRepository.countByContractStatus(ContractStatus.ACTIVE))
                .totalPaidRevenue(paidRevenue != null ? paidRevenue : BigDecimal.ZERO)
                .totalUnpaidAmount(unpaidAmount != null ? unpaidAmount : BigDecimal.ZERO)
                .unpaidBillsCount(paymentRepository.countByPaymentStatus(PaymentStatus.UNPAID))
                // Admin Action Queues
                .pendingLandlordRequests(upgradeRequestRepository.countByRequestStatus(LandlordStatus.PENDING))
                .pendingRoomReports(roomReportRepository.countByReportStatus(ReportStatus.PENDING))
                .pendingDisputeReports(disputeReportRepository.countByDisputeStatus(ReportStatus.PENDING))
                .pendingSupportRequests(supportRequestRepository.countBySupportStatus(ReportStatus.PENDING))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public com.roomkh.dto.response.LandlordDashboardResponseDTO getLandlordDashboard(Long userId) {
        LandlordProfile landlord = landlordProfileRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new com.roomkh.exception.ForbiddenException("Landlord profile not found"));

        Long landlordId = landlord.getLandlordProfileId();

        BigDecimal paidRevenue = paymentRepository.sumAmountByLandlordAndPaymentStatus(landlordId, PaymentStatus.PAID);
        BigDecimal unpaidAmount = paymentRepository.sumAmountByLandlordAndPaymentStatus(landlordId, PaymentStatus.UNPAID);

        return com.roomkh.dto.response.LandlordDashboardResponseDTO.builder()
                .landlordProfileId(landlordId)
                .businessName(landlord.getBusinessName())
                // Rooms
                .totalRooms(roomRepository.countByLandlord_LandlordProfileId(landlordId))
                .vacantRooms(roomRepository.countByLandlord_LandlordProfileIdAndRoomStatus(landlordId, RoomStatus.VACANT))
                .occupiedRooms(roomRepository.countByLandlord_LandlordProfileIdAndRoomStatus(landlordId, RoomStatus.OCCUPIED))
                .pendingModerationRooms(roomRepository.countByLandlord_LandlordProfileIdAndModerationStatus(landlordId, ModerationStatus.PENDING))
                .approvedRooms(roomRepository.countByLandlord_LandlordProfileIdAndModerationStatus(landlordId, ModerationStatus.APPROVED))
                // Tenancy
                .pendingBookings(bookingRepository.countByLandlord_LandlordProfileIdAndBookingStatus(landlordId, BookingStatus.PENDING))
                .activeContracts(contractRepository.countByLandlord_LandlordProfileIdAndContractStatus(landlordId, ContractStatus.ACTIVE))
                // Financials
                .totalPaidRevenue(paidRevenue != null ? paidRevenue : BigDecimal.ZERO)
                .totalUnpaidAmount(unpaidAmount != null ? unpaidAmount : BigDecimal.ZERO)
                .unpaidBillsCount(paymentRepository.countByLandlord_LandlordProfileIdAndPaymentStatus(landlordId, PaymentStatus.UNPAID))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public com.roomkh.dto.response.UserDashboardResponseDTO getUserDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new com.roomkh.exception.ResourceNotFoundException("User not found"));

        BigDecimal dueAmount = paymentRepository.sumAmountByUserAndPaymentStatus(userId, PaymentStatus.UNPAID);
        BigDecimal paidAmount = paymentRepository.sumAmountByUserAndPaymentStatus(userId, PaymentStatus.PAID);

        return com.roomkh.dto.response.UserDashboardResponseDTO.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .userSegment(user.getUserSegment().name())
                // Tenancy & Bookings
                .activeContracts(contractRepository.countByUser_UserIdAndContractStatus(userId, ContractStatus.ACTIVE))
                .pendingBookings(bookingRepository.countByUser_UserIdAndBookingStatus(userId, BookingStatus.PENDING))
                .totalBookings(bookingRepository.countByUser_UserId(userId))
                // Payments & Dues
                .unpaidBillsCount(paymentRepository.countByUser_UserIdAndPaymentStatus(userId, PaymentStatus.UNPAID))
                .totalDueAmount(dueAmount != null ? dueAmount : BigDecimal.ZERO)
                .totalPaidAmount(paidAmount != null ? paidAmount : BigDecimal.ZERO)
                // Activity & Engagement
                .unreadNotificationsCount(notificationRepository.countByUser_UserIdAndNotificationStatus(userId, NotificationStatus.UNREAD))
                .submittedRatingsCount(roomRatingRepository.countByUser_UserId(userId))
                .submittedSupportRequestsCount(supportRequestRepository.countByUser_UserId(userId))
                .build();
    }
}
