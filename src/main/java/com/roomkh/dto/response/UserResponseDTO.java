package com.roomkh.dto.response;

import com.roomkh.enums.LanguageType;
import com.roomkh.enums.UserRole;
import com.roomkh.enums.UserSegment;
import com.roomkh.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class UserResponseDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private UserSegment userSegment;
    private LanguageType language;
    private UserStatus status;
    private OffsetDateTime createdAt;
}
