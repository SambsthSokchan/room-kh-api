package com.roomkh.entity;

import com.roomkh.enums.PaymentMethod;
import com.roomkh.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "payments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"contract_id", "payment_month"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private RentalContract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private LandlordProfile landlord;

    @Column(name = "payment_month", nullable = false)
    private LocalDate paymentMonth;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "base_rent_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal baseRentAmount = BigDecimal.ZERO;

    @Column(name = "water_fee", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal waterFee = BigDecimal.ZERO;

    @Column(name = "electricity_fee", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal electricityFee = BigDecimal.ZERO;

    @Column(name = "other_fees", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal otherFees = BigDecimal.ZERO;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "payment_status", nullable = false, columnDefinition = "payment_status")
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "payment_method", columnDefinition = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "receipt_url", length = 255)
    private String receiptUrl;

    @Column(name = "payment_note", columnDefinition = "TEXT")
    private String paymentNote;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void calculateTotalAmount() {
        BigDecimal rent = baseRentAmount != null ? baseRentAmount : BigDecimal.ZERO;
        BigDecimal water = waterFee != null ? waterFee : BigDecimal.ZERO;
        BigDecimal electric = electricityFee != null ? electricityFee : BigDecimal.ZERO;
        BigDecimal other = otherFees != null ? otherFees : BigDecimal.ZERO;
        this.amount = rent.add(water).add(electric).add(other);
    }
}
