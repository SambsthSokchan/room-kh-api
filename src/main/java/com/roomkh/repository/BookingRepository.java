package com.roomkh.repository;

import com.roomkh.entity.Booking;
import com.roomkh.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    List<Booking> findByLandlord_LandlordProfileIdOrderByCreatedAtDesc(Long landlordProfileId);
    boolean existsByUser_UserIdAndRoom_RoomIdAndBookingStatus(
            Long userId,
            Long roomId,
            BookingStatus bookingStatus
    );
    long countByBookingStatus(BookingStatus bookingStatus);
    long countByLandlord_LandlordProfileIdAndBookingStatus(Long landlordProfileId, BookingStatus bookingStatus);
    long countByUser_UserIdAndBookingStatus(Long userId, BookingStatus bookingStatus);
    long countByUser_UserId(Long userId);
}
