package com.roomkh.service;

import com.roomkh.dto.request.RoomCommentRequestDTO;
import com.roomkh.dto.response.RoomCommentResponseDTO;

import java.util.List;

public interface RoomCommentService {
    RoomCommentResponseDTO addComment(Long userId, RoomCommentRequestDTO request);
    List<RoomCommentResponseDTO> getActiveCommentsByRoom(Long roomId);
    RoomCommentResponseDTO removeComment(Long commentId, Long requesterUserId, boolean isAdmin);
}
