package com.roomkh.dto.request;

import com.roomkh.enums.ReportStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportReviewRequestDTO {

    @NotNull(message = "Status is required (REVIEWED, APPROVED, or REJECTED)")
    private ReportStatus status;

    private String reviewNote;
}
