package com.roomkh.entity;

import com.roomkh.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private LandlordProfile landlord;

    @Column(name = "booking_date", nullable = false)
    @Builder.Default
    private LocalDate bookingDate = LocalDate.now();

    @Column(name = "move_in_date")
    private LocalDate moveInDate;

    @Column(name = "move_out_date")
    private LocalDate moveOutDate;

    @Column(name = "tenant_headcount", nullable = false)
    @Builder.Default
    private Short tenantHeadcount = 1;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "booking_status", nullable = false, columnDefinition = "booking_status")
    @Builder.Default
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    @Column(name = "booking_note", columnDefinition = "TEXT")
    private String bookingNote;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
