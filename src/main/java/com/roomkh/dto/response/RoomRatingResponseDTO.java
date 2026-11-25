package com.roomkh.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class RoomRatingResponseDTO {
    private Long ratingId;
    private Long roomId;
    private Long userId;
    private String reviewerName;
    private Short ratingValue;
    private String ratingComment;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
