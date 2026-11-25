package com.roomkh.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomRatingRequestDTO {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Rating value is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Short ratingValue;

    private String ratingComment;
}
