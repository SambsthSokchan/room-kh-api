package com.roomkh.service.impl;

import com.roomkh.dto.request.ReportReviewRequestDTO;
import com.roomkh.dto.request.SupportRequestDTO;
import com.roomkh.dto.response.SupportRequestResponseDTO;
import com.roomkh.entity.Admin;
import com.roomkh.entity.Notification;
import com.roomkh.entity.SupportRequest;
import com.roomkh.entity.User;
import com.roomkh.enums.NotificationStatus;
import com.roomkh.enums.ReportStatus;
import com.roomkh.exception.BadRequestException;
import com.roomkh.exception.ForbiddenException;
import com.roomkh.exception.ResourceNotFoundException;
import com.roomkh.repository.AdminRepository;
import com.roomkh.repository.NotificationRepository;
import com.roomkh.repository.SupportRequestRepository;
import com.roomkh.repository.UserRepository;
import com.roomkh.service.SupportRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportRequestServiceImpl implements SupportRequestService {

    private final SupportRequestRepository supportRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public SupportRequestResponseDTO submitSupportRequest(Long userId, SupportRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SupportRequest ticket = SupportRequest.builder()
                .user(user)
                .supportSubject(dto.getSupportSubject().trim())
                .supportDescription(dto.getSupportDescription().trim())
                .supportStatus(ReportStatus.PENDING)
                .build();

        return toResponse(supportRepository.save(ticket));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportRequestResponseDTO> getMySupportRequests(Long userId) {
        return supportRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportRequestResponseDTO> getPendingSupportRequests() {
        return supportRepository.findBySupportStatusOrderByCreatedAtAsc(ReportStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public SupportRequestResponseDTO reviewSupportRequest(
            Long supportId,
            Long adminUserId,
            ReportReviewRequestDTO review
    ) {
        if (review.getStatus() == ReportStatus.PENDING) {
            throw new BadRequestException("Review status must be REVIEWED, APPROVED, or REJECTED");
        }

        Admin admin = adminRepository.findByUser_UserId(adminUserId)
                .orElseThrow(() -> new ForbiddenException("Admin profile not found"));

        SupportRequest ticket = supportRepository.findById(supportId)
                .orElseThrow(() -> new ResourceNotFoundException("Support request not found"));

        ticket.setSupportStatus(review.getStatus());
        ticket.setReviewedBy(admin);
        ticket.setReviewNote(review.getReviewNote());

        SupportRequest saved = supportRepository.save(ticket);

        notificationRepository.save(Notification.builder()
                .user(ticket.getUser())
                .notificationType("SUPPORT_REQUEST")
                .notificationTitle("Support Ticket Updated")
                .notificationMessage("Your support request \"" + ticket.getSupportSubject()
                        + "\" was marked " + review.getStatus().name() + ".")
                .notificationStatus(NotificationStatus.UNREAD)
                .build());

        return toResponse(saved);
    }

    private SupportRequestResponseDTO toResponse(SupportRequest s) {
        return SupportRequestResponseDTO.builder()
                .supportId(s.getSupportId())
                .userId(s.getUser().getUserId())
                .userName(s.getUser().getFullName())
                .supportSubject(s.getSupportSubject())
                .supportDescription(s.getSupportDescription())
                .supportStatus(s.getSupportStatus())
                .reviewedByAdminId(s.getReviewedBy() != null ? s.getReviewedBy().getAdminId() : null)
                .reviewNote(s.getReviewNote())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
