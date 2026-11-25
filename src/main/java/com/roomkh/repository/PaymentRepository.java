package com.roomkh.repository;

import com.roomkh.entity.Payment;
import com.roomkh.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByContract_ContractIdOrderByPaymentMonthDesc(Long contractId);
    List<Payment> findByUser_UserIdOrderByPaymentMonthDesc(Long userId);
    List<Payment> findByLandlord_LandlordProfileIdOrderByPaymentMonthDesc(Long landlordProfileId);
    boolean existsByContract_ContractIdAndPaymentMonth(Long contractId, LocalDate paymentMonth);
    long countByPaymentStatus(PaymentStatus paymentStatus);
    long countByLandlord_LandlordProfileIdAndPaymentStatus(Long landlordProfileId, PaymentStatus paymentStatus);
    long countByUser_UserIdAndPaymentStatus(Long userId, PaymentStatus paymentStatus);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus = :status")
    BigDecimal sumAmountByPaymentStatus(@Param("status") PaymentStatus status);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.landlord.landlordProfileId = :landlordProfileId AND p.paymentStatus = :status")
    BigDecimal sumAmountByLandlordAndPaymentStatus(@Param("landlordProfileId") Long landlordProfileId, @Param("status") PaymentStatus status);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.user.userId = :userId AND p.paymentStatus = :status")
    BigDecimal sumAmountByUserAndPaymentStatus(@Param("userId") Long userId, @Param("status") PaymentStatus status);
}
