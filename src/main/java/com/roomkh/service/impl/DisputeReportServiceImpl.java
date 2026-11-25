package com.roomkh.service.impl;

import com.roomkh.dto.request.DisputeReportRequestDTO;
import com.roomkh.dto.request.ReportReviewRequestDTO;
import com.roomkh.dto.response.DisputeReportResponseDTO;
import com.roomkh.entity.*;
import com.roomkh.enums.NotificationStatus;
import com.roomkh.enums.ReportStatus;
import com.roomkh.exception.BadRequestException;
import com.roomkh.exception.ForbiddenException;
import com.roomkh.exception.ResourceNotFoundException;
import com.roomkh.repository.*;
import com.roomkh.service.DisputeReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisputeReportServiceImpl implements DisputeReportService {

    private final DisputeReportRepository disputeRepository;
    private final UserRepository userRepository;
    private final LandlordProfileRepository landlordProfileRepository;
    private final RoomRepository roomRepository;
    private final AdminRepository adminRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public DisputeReportResponseDTO submitDispute(Long userId, DisputeReportRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LandlordProfile landlord = landlordProfileRepository.findById(dto.getLandlordId())
                .orElseThrow(() -> new ResourceNotFoundException("Landlord profile not found"));

        Room room = null;
        if (dto.getRoomId() != null) {
            room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        }

        DisputeReport dispute = DisputeReport.builder()
                .user(user)
                .landlord(landlord)
                .room(room)
                .disputeType(dto.getDisputeType().trim())
                .disputeDescription(dto.getDisputeDescription().trim())
                .disputeEvidenceUrl(dto.getDisputeEvidenceUrl())
                .disputeStatus(ReportStatus.PENDING)
                .build();

        return toResponse(disputeRepository.save(dispute));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisputeReportResponseDTO> getMyDisputes(Long userId) {
        return disputeRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisputeReportResponseDTO> getPendingDisputes() {
        return disputeRepository.findByDisputeStatusOrderByCreatedAtAsc(ReportStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DisputeReportResponseDTO reviewDispute(Long disputeId, Long adminUserId, ReportReviewRequestDTO review) {
        if (review.getStatus() == ReportStatus.PENDING) {
            throw new BadRequestException("Review status must be REVIEWED, APPROVED, or REJECTED");
        }

        Admin admin = adminRepository.findByUser_UserId(adminUserId)
                .orElseThrow(() -> new ForbiddenException("Admin profile not found"));

        DisputeReport dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute report not found"));

        dispute.setDisputeStatus(review.getStatus());
        dispute.setReviewedBy(admin);
        dispute.setReviewNote(review.getReviewNote());

        DisputeReport saved = disputeRepository.save(dispute);

        notificationRepository.save(Notification.builder()
                .user(dispute.getUser())
                .notificationType("DISPUTE_REPORT")
                .notificationTitle("Dispute Case Updated")
                .notificationMessage("Your dispute (" + dispute.getDisputeType()
                        + ") was marked " + review.getStatus().name() + ".")
                .notificationStatus(NotificationStatus.UNREAD)
                .build());

        return toResponse(saved);
    }

    private DisputeReportResponseDTO toResponse(DisputeReport d) {
        return DisputeReportResponseDTO.builder()
                .disputeId(d.getDisputeId())
                .userId(d.getUser().getUserId())
                .reporterName(d.getUser().getFullName())
                .landlordId(d.getLandlord().getLandlordProfileId())
                .businessName(d.getLandlord().getBusinessName())
                .roomId(d.getRoom() != null ? d.getRoom().getRoomId() : null)
                .disputeType(d.getDisputeType())
                .disputeDescription(d.getDisputeDescription())
                .disputeEvidenceUrl(d.getDisputeEvidenceUrl())
                .disputeStatus(d.getDisputeStatus())
                .reviewedByAdminId(d.getReviewedBy() != null ? d.getReviewedBy().getAdminId() : null)
                .reviewNote(d.getReviewNote())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
