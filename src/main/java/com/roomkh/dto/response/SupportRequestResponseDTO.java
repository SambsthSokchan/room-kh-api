package com.roomkh.dto.response;

import com.roomkh.enums.ReportStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class SupportRequestResponseDTO {
    private Long supportId;
    private Long userId;
    private String userName;
    private String supportSubject;
    private String supportDescription;
    private ReportStatus supportStatus;
    private Long reviewedByAdminId;
    private String reviewNote;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
