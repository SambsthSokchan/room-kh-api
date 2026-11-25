package com.roomkh.dto.response;

import com.roomkh.enums.ReportStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class RoomReportResponseDTO {
    private Long roomReportId;
    private Long userId;
    private String reporterName;
    private Long roomId;
    private String roomTitle;
    private String reportReason;
    private String reportDescription;
    private String reportEvidenceUrl;
    private ReportStatus reportStatus;
    private Long reviewedByAdminId;
    private String reviewNote;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
