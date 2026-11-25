package com.roomkh.dto.request;

import com.roomkh.enums.ModerationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomModerationRequestDTO {

    @NotNull(message = "Decision is required (APPROVED or REJECTED)")
    private ModerationStatus decision;

    private String moderationNote;
}
