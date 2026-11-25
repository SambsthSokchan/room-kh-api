package com.roomkh.service.impl;

import com.roomkh.dto.request.RoomCommentRequestDTO;
import com.roomkh.dto.response.RoomCommentResponseDTO;
import com.roomkh.entity.Room;
import com.roomkh.entity.RoomComment;
import com.roomkh.entity.User;
import com.roomkh.enums.CommentStatus;
import com.roomkh.enums.ModerationStatus;
import com.roomkh.exception.BadRequestException;
import com.roomkh.exception.ForbiddenException;
import com.roomkh.exception.ResourceNotFoundException;
import com.roomkh.repository.RoomCommentRepository;
import com.roomkh.repository.RoomRepository;
import com.roomkh.repository.UserRepository;
import com.roomkh.service.RoomCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomCommentServiceImpl implements RoomCommentService {

    private final RoomCommentRepository commentRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RoomCommentResponseDTO addComment(Long userId, RoomCommentRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (room.getModerationStatus() != ModerationStatus.APPROVED) {
            throw new BadRequestException("Only approved rooms can receive comments");
        }

        RoomComment comment = RoomComment.builder()
                .user(user)
                .room(room)
                .commentText(dto.getCommentText().trim())
                .commentStatus(CommentStatus.ACTIVE)
                .build();

        return toResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomCommentResponseDTO> getActiveCommentsByRoom(Long roomId) {
        return commentRepository.findByRoom_RoomIdAndCommentStatusOrderByCreatedAtDesc(
                        roomId, CommentStatus.ACTIVE
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public RoomCommentResponseDTO removeComment(Long commentId, Long requesterUserId, boolean isAdmin) {
        RoomComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        boolean isOwner = comment.getUser().getUserId().equals(requesterUserId);
        if (!isAdmin && !isOwner) {
            throw new ForbiddenException("You can only remove your own comment");
        }

        comment.setCommentStatus(isAdmin && !isOwner ? CommentStatus.HIDDEN : CommentStatus.DELETED);
        return toResponse(commentRepository.save(comment));
    }

    private RoomCommentResponseDTO toResponse(RoomComment c) {
        return RoomCommentResponseDTO.builder()
                .commentId(c.getCommentId())
                .roomId(c.getRoom().getRoomId())
                .userId(c.getUser().getUserId())
                .authorName(c.getUser().getFullName())
                .commentText(c.getCommentText())
                .commentStatus(c.getCommentStatus())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
