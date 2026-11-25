package com.roomkh.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "room_photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "photo_url", nullable = false, length = 255)
    private String photoUrl;

    @Column(name = "photo_type", length = 50)
    private String photoType;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
