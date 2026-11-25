package com.roomkh.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomCommentRequestDTO {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotBlank(message = "Comment text is required")
    private String commentText;
}
