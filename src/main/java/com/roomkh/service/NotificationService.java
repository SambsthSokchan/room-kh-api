package com.roomkh.service;

import com.roomkh.dto.response.NotificationResponseDTO;

import java.util.List;

public interface NotificationService {
    List<NotificationResponseDTO> getMyNotifications(Long userId);
    long getUnreadCount(Long userId);
    NotificationResponseDTO markAsRead(Long notificationId, Long userId);
}
