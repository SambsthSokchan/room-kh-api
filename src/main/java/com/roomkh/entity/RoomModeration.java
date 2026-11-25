package com.roomkh.entity;

import com.roomkh.enums.ModerationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "room_moderations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomModeration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "moderation_id")
    private Long moderationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "decision", nullable = false, columnDefinition = "moderation_status")
    private ModerationStatus decision;

    @Column(name = "moderation_note", columnDefinition = "TEXT")
    private String moderationNote;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
