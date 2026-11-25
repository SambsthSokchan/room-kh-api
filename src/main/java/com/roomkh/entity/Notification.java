package com.roomkh.entity;

import com.roomkh.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType;

    @Column(name = "notification_title", nullable = false, length = 150)
    private String notificationTitle;

    @Column(name = "notification_message", nullable = false, columnDefinition = "TEXT")
    private String notificationMessage;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "notification_status", nullable = false, columnDefinition = "notification_status")
    @Builder.Default
    private NotificationStatus notificationStatus = NotificationStatus.UNREAD;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "read_at")
    private OffsetDateTime readAt;
}
