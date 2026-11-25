package com.roomkh.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DisputeReportRequestDTO {

    @NotNull(message = "Landlord ID is required")
    private Long landlordId;

    // Optional: link the dispute to a specific room
    private Long roomId;

    @NotBlank(message = "Dispute type is required")
    @Size(max = 100)
    private String disputeType;

    @NotBlank(message = "Dispute description is required")
    private String disputeDescription;

    @Size(max = 255)
    private String disputeEvidenceUrl;
}
