package com.roomkh.service.impl;

import com.roomkh.dto.request.ReportReviewRequestDTO;
import com.roomkh.dto.request.RoomReportRequestDTO;
import com.roomkh.dto.response.RoomReportResponseDTO;
import com.roomkh.entity.*;
import com.roomkh.enums.NotificationStatus;
import com.roomkh.enums.ReportStatus;
import com.roomkh.exception.BadRequestException;
import com.roomkh.exception.ForbiddenException;
import com.roomkh.exception.ResourceNotFoundException;
import com.roomkh.repository.*;
import com.roomkh.service.RoomReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomReportServiceImpl implements RoomReportService {

    private final RoomReportRepository roomReportRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final AdminRepository adminRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public RoomReportResponseDTO submitReport(Long userId, RoomReportRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        RoomReport report = RoomReport.builder()
                .user(user)
                .room(room)
                .reportReason(dto.getReportReason().trim())
                .reportDescription(dto.getReportDescription())
                .reportEvidenceUrl(dto.getReportEvidenceUrl())
                .reportStatus(ReportStatus.PENDING)
                .build();

        return toResponse(roomReportRepository.save(report));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomReportResponseDTO> getMyReports(Long userId) {
        return roomReportRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomReportResponseDTO> getPendingReports() {
        return roomReportRepository.findByReportStatusOrderByCreatedAtAsc(ReportStatus.PENDING)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public RoomReportResponseDTO reviewReport(Long reportId, Long adminUserId, ReportReviewRequestDTO review) {
        if (review.getStatus() == ReportStatus.PENDING) {
            throw new BadRequestException("Review status must be REVIEWED, APPROVED, or REJECTED");
        }

        Admin admin = adminRepository.findByUser_UserId(adminUserId)
                .orElseThrow(() -> new ForbiddenException("Admin profile not found"));

        RoomReport report = roomReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Room report not found"));

        report.setReportStatus(review.getStatus());
        report.setReviewedBy(admin);
        report.setReviewNote(review.getReviewNote());

        RoomReport saved = roomReportRepository.save(report);

        notificationRepository.save(Notification.builder()
                .user(report.getUser())
                .notificationType("ROOM_REPORT")
                .notificationTitle("Room Report Update")
                .notificationMessage("Your report on \"" + report.getRoom().getRoomTitle()
                        + "\" was marked " + review.getStatus().name() + ".")
                .notificationStatus(NotificationStatus.UNREAD)
                .build());

        return toResponse(saved);
    }

    private RoomReportResponseDTO toResponse(RoomReport r) {
        return RoomReportResponseDTO.builder()
                .roomReportId(r.getRoomReportId())
                .userId(r.getUser().getUserId())
                .reporterName(r.getUser().getFullName())
                .roomId(r.getRoom().getRoomId())
                .roomTitle(r.getRoom().getRoomTitle())
                .reportReason(r.getReportReason())
                .reportDescription(r.getReportDescription())
                .reportEvidenceUrl(r.getReportEvidenceUrl())
                .reportStatus(r.getReportStatus())
                .reviewedByAdminId(r.getReviewedBy() != null ? r.getReviewedBy().getAdminId() : null)
                .reviewNote(r.getReviewNote())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
