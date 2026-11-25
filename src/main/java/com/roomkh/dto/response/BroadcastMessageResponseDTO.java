package com.roomkh.dto.response;

import com.roomkh.enums.BroadcastAudience;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class BroadcastMessageResponseDTO {
    private Long broadcastId;
    private Long adminId;
    private BroadcastAudience targetAudience;
    private String subject;
    private String body;
    private Integer recipientsNotified;
    private OffsetDateTime sentAt;
}
