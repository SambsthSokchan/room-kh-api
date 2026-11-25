package com.roomkh.dto.request;

import com.roomkh.enums.AdminUserAction;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserManagementRequestDTO {

    @NotNull(message = "Action type is required (ACTIVATE, SUSPEND, BLOCK, UNBLOCK, or DELETE)")
    private AdminUserAction actionType;

    private String managementNote;
}
