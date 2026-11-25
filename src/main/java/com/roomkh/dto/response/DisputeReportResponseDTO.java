package com.roomkh.dto.response;

import com.roomkh.enums.ReportStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class DisputeReportResponseDTO {
    private Long disputeId;
    private Long userId;
    private String reporterName;
    private Long landlordId;
    private String businessName;
    private Long roomId;
    private String disputeType;
    private String disputeDescription;
    private String disputeEvidenceUrl;
    private ReportStatus disputeStatus;
    private Long reviewedByAdminId;
    private String reviewNote;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
