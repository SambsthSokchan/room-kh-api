package com.roomkh.mapper;

import com.roomkh.dto.response.NotificationResponseDTO;
import com.roomkh.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponseDTO toResponse(Notification notification) {
        return NotificationResponseDTO.builder()
                .notificationId(notification.getNotificationId())
                .notificationType(notification.getNotificationType())
                .notificationTitle(notification.getNotificationTitle())
                .notificationMessage(notification.getNotificationMessage())
                .notificationStatus(notification.getNotificationStatus())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}
