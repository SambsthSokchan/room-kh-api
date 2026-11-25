package com.roomkh.service.impl;

import com.roomkh.dto.request.BroadcastMessageRequestDTO;
import com.roomkh.dto.request.RoomModerationRequestDTO;
import com.roomkh.dto.request.UserManagementRequestDTO;
import com.roomkh.dto.response.*;
import com.roomkh.entity.*;
import com.roomkh.enums.*;
import com.roomkh.exception.BadRequestException;
import com.roomkh.exception.ConflictException;
import com.roomkh.exception.ForbiddenException;
import com.roomkh.exception.ResourceNotFoundException;
import com.roomkh.mapper.RoomMapper;
import com.roomkh.repository.*;
import com.roomkh.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RentalContractRepository contractRepository;
    private final RoomModerationRepository roomModerationRepository;
    private final UserManagementRepository userManagementRepository;
    private final BroadcastMessageRepository broadcastMessageRepository;
    private final NotificationRepository notificationRepository;
    private final RoomMapper roomMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getPendingRooms() {
        return roomRepository.findByModerationStatusOrderByCreatedAtAsc(ModerationStatus.PENDING)
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public RoomModerationResponseDTO moderateRoom(
            Long roomId,
            Long adminUserId,
            RoomModerationRequestDTO dto
    ) {
        if (dto.getDecision() != ModerationStatus.APPROVED
                && dto.getDecision() != ModerationStatus.REJECTED) {
            throw new BadRequestException("Decision must be APPROVED or REJECTED");
        }

        if (dto.getDecision() == ModerationStatus.REJECTED
                && (dto.getModerationNote() == null || dto.getModerationNote().isBlank())) {
            throw new BadRequestException("Moderation note is required when rejecting a room");
        }

        Admin admin = getAdminOrThrow(adminUserId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        RoomModeration moderation = RoomModeration.builder()
                .room(room)
                .admin(admin)
                .decision(dto.getDecision())
                .moderationNote(dto.getModerationNote())
                .build();

        // Flush immediately so PostgreSQL trigger trg_sync_room_moderation updates rooms.moderation_status
        RoomModeration saved = roomModerationRepository.saveAndFlush(moderation);

        // Notify the landlord about the moderation decision
        String title = dto.getDecision() == ModerationStatus.APPROVED
                ? "Room Listing Approved"
                : "Room Listing Rejected";
        String body = dto.getDecision() == ModerationStatus.APPROVED
                ? "Your room \"" + room.getRoomTitle() + "\" is now live on RoomKH."
                : "Your room \"" + room.getRoomTitle() + "\" was rejected: " + dto.getModerationNote();

        notificationRepository.save(Notification.builder()
                .user(room.getLandlord().getUser())
                .notificationType("ROOM_MODERATION")
                .notificationTitle(title)
                .notificationMessage(body)
                .notificationStatus(NotificationStatus.UNREAD)
                .build());

        return RoomModerationResponseDTO.builder()
                .moderationId(saved.getModerationId())
                .roomId(room.getRoomId())
                .roomTitle(room.getRoomTitle())
                .adminId(admin.getAdminId())
                .decision(saved.getDecision())
                .moderationNote(saved.getModerationNote())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public UserManagementResponseDTO manageUser(
            Long targetUserId,
            Long adminUserId,
            UserManagementRequestDTO dto
    ) {
        Admin admin = getAdminOrThrow(adminUserId);

        if (targetUserId.equals(adminUserId)) {
            throw new BadRequestException("You cannot apply account status actions to your own admin account");
        }

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

        if (target.getRole() == UserRole.ADMIN) {
            throw new ForbiddenException("Cannot modify another ADMIN account through this endpoint");
        }

        UserStatus newStatus = switch (dto.getActionType()) {
            case ACTIVATE, UNBLOCK -> UserStatus.ACTIVE;
            case SUSPEND -> UserStatus.SUSPENDED;
            case BLOCK -> UserStatus.BLOCKED;
            case DELETE -> {
                // Mirror trg_prevent_user_soft_delete for a clean 409 Conflict response
                if (contractRepository.existsByUser_UserIdAndContractStatus(targetUserId, ContractStatus.ACTIVE)) {
                    throw new ConflictException(
                            "Cannot delete user " + targetUserId + " while an ACTIVE rental contract exists"
                    );
                }
                yield UserStatus.DELETED;
            }
            case APPROVE_LANDLORD, REJECT_LANDLORD -> throw new BadRequestException(
                    "Use PATCH /api/landlord-upgrade-requests/{requestId}/review for landlord approvals"
            );
        };

        target.setStatus(newStatus);
        userRepository.saveAndFlush(target);

        UserManagement log = UserManagement.builder()
                .admin(admin)
                .targetUser(target)
                .actionType(dto.getActionType())
                .managementNote(dto.getManagementNote())
                .build();

        UserManagement saved = userManagementRepository.saveAndFlush(log);

        return UserManagementResponseDTO.builder()
                .managementId(saved.getManagementId())
                .adminId(admin.getAdminId())
                .targetUserId(target.getUserId())
                .targetUserEmail(target.getEmail())
                .actionType(saved.getActionType())
                .resultingStatus(newStatus)
                .managementNote(saved.getManagementNote())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserManagementResponseDTO> getUserAuditHistory(Long targetUserId) {
        return userManagementRepository.findByTargetUser_UserIdOrderByCreatedAtDesc(targetUserId)
                .stream()
                .map(log -> UserManagementResponseDTO.builder()
                        .managementId(log.getManagementId())
                        .adminId(log.getAdmin().getAdminId())
                        .targetUserId(log.getTargetUser().getUserId())
                        .targetUserEmail(log.getTargetUser().getEmail())
                        .actionType(log.getActionType())
                        .resultingStatus(log.getTargetUser().getStatus())
                        .managementNote(log.getManagementNote())
                        .createdAt(log.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public BroadcastMessageResponseDTO sendBroadcast(Long adminUserId, BroadcastMessageRequestDTO dto) {
        Admin admin = getAdminOrThrow(adminUserId);

        BroadcastMessage broadcast = BroadcastMessage.builder()
                .admin(admin)
                .targetAudience(dto.getTargetAudience())
                .subject(dto.getSubject().trim())
                .body(dto.getBody().trim())
                .build();

        BroadcastMessage saved = broadcastMessageRepository.saveAndFlush(broadcast);

        List<User> recipients = switch (dto.getTargetAudience()) {
            case ALL_USERS -> userRepository.findByStatus(UserStatus.ACTIVE);
            case STUDENTS -> userRepository.findByUserSegmentAndStatus(UserSegment.STUDENT, UserStatus.ACTIVE);
            case WORKERS -> userRepository.findByUserSegmentAndStatus(UserSegment.WORKER, UserStatus.ACTIVE);
            case LANDLORDS -> userRepository.findByRoleAndStatus(UserRole.LANDLORD, UserStatus.ACTIVE);
        };

        List<Notification> notifications = recipients.stream()
                .map(recipient -> Notification.builder()
                        .user(recipient)
                        .notificationType("BROADCAST")
                        .notificationTitle(saved.getSubject())
                        .notificationMessage(saved.getBody())
                        .notificationStatus(NotificationStatus.UNREAD)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);

        return BroadcastMessageResponseDTO.builder()
                .broadcastId(saved.getBroadcastId())
                .adminId(admin.getAdminId())
                .targetAudience(saved.getTargetAudience())
                .subject(saved.getSubject())
                .body(saved.getBody())
                .recipientsNotified(notifications.size())
                .sentAt(saved.getSentAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BroadcastMessageResponseDTO> getBroadcastHistory() {
        return broadcastMessageRepository.findAllByOrderBySentAtDesc()
                .stream()
                .map(b -> BroadcastMessageResponseDTO.builder()
                        .broadcastId(b.getBroadcastId())
                        .adminId(b.getAdmin().getAdminId())
                        .targetAudience(b.getTargetAudience())
                        .subject(b.getSubject())
                        .body(b.getBody())
                        .sentAt(b.getSentAt())
                        .build())
                .toList();
    }

    private Admin getAdminOrThrow(Long adminUserId) {
        return adminRepository.findByUser_UserId(adminUserId)
                .orElseThrow(() -> new ForbiddenException("Admin profile not found"));
    }
}
