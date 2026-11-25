package com.roomkh.dto.response;

import com.roomkh.enums.NotificationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class NotificationResponseDTO {
    private Long notificationId;
    private String notificationType;
    private String notificationTitle;
    private String notificationMessage;
    private NotificationStatus notificationStatus;
    private OffsetDateTime createdAt;
    private OffsetDateTime readAt;
}
