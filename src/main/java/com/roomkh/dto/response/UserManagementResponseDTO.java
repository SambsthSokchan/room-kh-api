package com.roomkh.dto.response;

import com.roomkh.enums.AdminUserAction;
import com.roomkh.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class UserManagementResponseDTO {
    private Long managementId;
    private Long adminId;
    private Long targetUserId;
    private String targetUserEmail;
    private AdminUserAction actionType;
    private UserStatus resultingStatus;
    private String managementNote;
    private OffsetDateTime createdAt;
}
