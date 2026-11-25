package com.roomkh.dto.request;

import com.roomkh.enums.BroadcastAudience;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BroadcastMessageRequestDTO {

    @NotNull(message = "Target audience is required (ALL_USERS, STUDENTS, WORKERS, or LANDLORDS)")
    private BroadcastAudience targetAudience;

    @NotBlank(message = "Subject is required")
    @Size(max = 150)
    private String subject;

    @NotBlank(message = "Message body is required")
    private String body;
}
