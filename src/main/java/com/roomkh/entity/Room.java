package com.roomkh.entity;

import com.roomkh.enums.ModerationStatus;
import com.roomkh.enums.RoomStatus;
import com.roomkh.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private LandlordProfile landlord;

    @Column(name = "room_title", nullable = false, length = 150)
    private String roomTitle;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "room_type", nullable = false, columnDefinition = "room_type")
    private RoomType roomType;

    @Column(name = "monthly_rent", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyRent;

    @Column(name = "room_address", nullable = false, length = 255)
    private String roomAddress;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "nearby_university", length = 150)
    private String nearbyUniversity;

    @Column(name = "room_description", columnDefinition = "TEXT")
    private String roomDescription;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "room_status", nullable = false, columnDefinition = "room_status")
    @Builder.Default
    private RoomStatus roomStatus = RoomStatus.VACANT;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "moderation_status", nullable = false, columnDefinition = "moderation_status")
    @Builder.Default
    private ModerationStatus moderationStatus = ModerationStatus.PENDING;

    @Column(name = "average_rating", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRating = new BigDecimal("0.00");

    @Column(name = "rating_count", nullable = false)
    @Builder.Default
    private Integer ratingCount = 0;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoomPhoto> photos = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoomFacility> facilities = new ArrayList<>();

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
