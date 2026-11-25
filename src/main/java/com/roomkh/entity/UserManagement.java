package com.roomkh.entity;

import com.roomkh.enums.AdminUserAction;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_managements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "management_id")
    private Long managementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "action_type", nullable = false, columnDefinition = "admin_user_action")
    private AdminUserAction actionType;

    @Column(name = "management_note", columnDefinition = "TEXT")
    private String managementNote;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
