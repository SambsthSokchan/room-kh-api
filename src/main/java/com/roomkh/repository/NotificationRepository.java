package com.roomkh.repository;

import com.roomkh.entity.Notification;
import com.roomkh.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    long countByUser_UserIdAndNotificationStatus(Long userId, NotificationStatus status);
}
