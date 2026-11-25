package com.roomkh.dto.response;

import com.roomkh.enums.ModerationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class RoomModerationResponseDTO {
    private Long moderationId;
    private Long roomId;
    private String roomTitle;
    private Long adminId;
    private ModerationStatus decision;
    private String moderationNote;
    private OffsetDateTime createdAt;
}
