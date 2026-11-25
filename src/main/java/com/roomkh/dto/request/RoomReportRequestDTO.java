package com.roomkh.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoomReportRequestDTO {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotBlank(message = "Report reason is required")
    @Size(max = 150)
    private String reportReason;

    private String reportDescription;

    @Size(max = 255)
    private String reportEvidenceUrl;
}
