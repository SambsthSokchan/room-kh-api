package com.roomkh.service.impl;

import com.roomkh.dto.response.NotificationResponseDTO;
import com.roomkh.entity.Notification;
import com.roomkh.enums.NotificationStatus;
import com.roomkh.exception.ForbiddenException;
import com.roomkh.exception.ResourceNotFoundException;
import com.roomkh.mapper.NotificationMapper;
import com.roomkh.repository.NotificationRepository;
import com.roomkh.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getMyNotifications(Long userId) {
        return notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUser_UserIdAndNotificationStatus(
                userId, NotificationStatus.UNREAD
        );
    }

    @Override
    @Transactional
    public NotificationResponseDTO markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException("You cannot modify another user's notification");
        }

        if (notification.getNotificationStatus() != NotificationStatus.READ) {
            notification.setNotificationStatus(NotificationStatus.READ);
            notification.setReadAt(OffsetDateTime.now());
            notification = notificationRepository.save(notification);
        }

        return notificationMapper.toResponse(notification);
    }
}
