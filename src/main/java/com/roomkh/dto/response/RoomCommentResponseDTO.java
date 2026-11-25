package com.roomkh.dto.response;

import com.roomkh.enums.CommentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class RoomCommentResponseDTO {
    private Long commentId;
    private Long roomId;
    private Long userId;
    private String authorName;
    private String commentText;
    private CommentStatus commentStatus;
    private OffsetDateTime createdAt;
}
