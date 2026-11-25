package com.roomkh.repository;

import com.roomkh.entity.RoomComment;
import com.roomkh.enums.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomCommentRepository extends JpaRepository<RoomComment, Long> {
    List<RoomComment> findByRoom_RoomIdAndCommentStatusOrderByCreatedAtDesc(
            Long roomId,
            CommentStatus commentStatus
    );
}
