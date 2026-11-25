package com.roomkh.entity;

import com.roomkh.enums.BusinessType;
import com.roomkh.enums.IdCardType;
import com.roomkh.enums.LandlordStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "landlord_upgrade_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LandlordUpgradeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "business_name", length = 150)
    private String businessName;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "business_type", nullable = false, columnDefinition = "business_type")
    @Builder.Default
    private BusinessType businessType = BusinessType.RENTAL_HOUSE;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Column(name = "business_email", length = 150)
    private String businessEmail;

    @Column(name = "business_address", length = 255)
    private String businessAddress;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "city", length = 100)
    private String city;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "id_type", nullable = false, columnDefinition = "id_card_type")
    @Builder.Default
    private IdCardType idType = IdCardType.NATIONAL_ID;

    @Column(name = "id_number", length = 50)
    private String idNumber;

    @Column(name = "id_photo_url", length = 255)
    private String idPhotoUrl;

    @Column(name = "business_photo_url", length = 255)
    private String businessPhotoUrl;

    @Column(name = "business_description", columnDefinition = "TEXT")
    private String businessDescription;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "request_status", nullable = false, columnDefinition = "landlord_status")
    @Builder.Default
    private LandlordStatus requestStatus = LandlordStatus.PENDING;

    @Column(name = "review_note", columnDefinition = "TEXT")
    private String reviewNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private Admin reviewedBy;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
