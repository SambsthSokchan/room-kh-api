package com.roomkh.service.impl;

import com.roomkh.dto.request.BookingRequestDTO;
import com.roomkh.dto.request.BookingReviewRequestDTO;
import com.roomkh.dto.response.BookingResponseDTO;
import com.roomkh.entity.Booking;
import com.roomkh.entity.LandlordProfile;
import com.roomkh.entity.Room;
import com.roomkh.entity.User;
import com.roomkh.enums.BookingStatus;
import com.roomkh.enums.ModerationStatus;
import com.roomkh.enums.RoomStatus;
import com.roomkh.exception.BadRequestException;
import com.roomkh.exception.ConflictException;
import com.roomkh.exception.ForbiddenException;
import com.roomkh.exception.ResourceNotFoundException;
import com.roomkh.mapper.BookingMapper;
import com.roomkh.repository.BookingRepository;
import com.roomkh.repository.LandlordProfileRepository;
import com.roomkh.repository.RoomRepository;
import com.roomkh.repository.UserRepository;
import com.roomkh.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final LandlordProfileRepository landlordProfileRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(Long renterUserId, BookingRequestDTO dto) {
        User renter = userRepository.findById(renterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (room.getLandlord().getUser().getUserId().equals(renterUserId)) {
            throw new BadRequestException("You cannot book your own room");
        }

        if (room.getModerationStatus() != ModerationStatus.APPROVED) {
            throw new BadRequestException("Room is not approved for public booking");
        }

        if (room.getRoomStatus() != RoomStatus.VACANT) {
            throw new ConflictException("Room is currently " + room.getRoomStatus().name() + " and cannot be booked");
        }

        if (bookingRepository.existsByUser_UserIdAndRoom_RoomIdAndBookingStatus(
                renterUserId, room.getRoomId(), BookingStatus.PENDING)) {
            throw new ConflictException("You already have a pending booking request for this room");
        }

        LocalDate today = LocalDate.now();
        if (dto.getMoveInDate() != null && dto.getMoveInDate().isBefore(today)) {
            throw new BadRequestException("Move-in date cannot be earlier than today");
        }
        if (dto.getMoveOutDate() != null && dto.getMoveInDate() == null) {
            throw new BadRequestException("Move-in date is required when move-out date is specified");
        }
        if (dto.getMoveOutDate() != null && !dto.getMoveOutDate().isAfter(dto.getMoveInDate())) {
            throw new BadRequestException("Move-out date must be after move-in date");
        }

        String contact = (dto.getContactNumber() != null && !dto.getContactNumber().isBlank())
                ? dto.getContactNumber().trim()
                : renter.getPhoneNumber();

        Booking booking = Booking.builder()
                .user(renter)
                .room(room)
                .landlord(room.getLandlord())
                .bookingDate(today)
                .moveInDate(dto.getMoveInDate())
                .moveOutDate(dto.getMoveOutDate())
                .tenantHeadcount(dto.getTenantHeadcount())
                .contactNumber(contact)
                .bookingStatus(BookingStatus.PENDING)
                .bookingNote(dto.getBookingNote())
                .build();

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getMyBookings(Long renterUserId) {
        return bookingRepository.findByUser_UserIdOrderByCreatedAtDesc(renterUserId)
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Long bookingId, Long renterUserId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUser().getUserId().equals(renterUserId)) {
            throw new ForbiddenException("You can only cancel your own booking");
        }

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new ConflictException("Only PENDING bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getLandlordBookings(Long landlordUserId) {
        LandlordProfile landlord = landlordProfileRepository.findByUser_UserId(landlordUserId)
                .orElseThrow(() -> new ForbiddenException("Landlord profile not found"));

        return bookingRepository.findByLandlord_LandlordProfileIdOrderByCreatedAtDesc(
                        landlord.getLandlordProfileId()
                )
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public BookingResponseDTO reviewBooking(Long bookingId, Long landlordUserId, BookingReviewRequestDTO review) {
        if (review.getDecision() != BookingStatus.APPROVED
                && review.getDecision() != BookingStatus.REJECTED) {
            throw new BadRequestException("Decision must be APPROVED or REJECTED");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getLandlord().getUser().getUserId().equals(landlordUserId)) {
            throw new ForbiddenException("You do not own the room for this booking");
        }

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new ConflictException("Only PENDING bookings can be reviewed");
        }

        booking.setBookingStatus(review.getDecision());
        if (review.getBookingNote() != null && !review.getBookingNote().isBlank()) {
            booking.setBookingNote(review.getBookingNote().trim());
        }

        if (review.getDecision() == BookingStatus.APPROVED) {
            Room room = booking.getRoom();
            if (room.getRoomStatus() != RoomStatus.VACANT) {
                throw new ConflictException("Room is no longer vacant");
            }
            // Reserve the room until the rental contract is signed
            room.setRoomStatus(RoomStatus.RESERVED);
            roomRepository.save(room);
        }

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }
}
