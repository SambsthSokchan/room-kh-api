package com.roomkh.specification;

import com.roomkh.entity.Room;
import com.roomkh.entity.RoomFacility;
import com.roomkh.enums.ModerationStatus;
import com.roomkh.enums.RoomStatus;
import com.roomkh.enums.RoomType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class RoomSpecification {

    private RoomSpecification() {
    }

    public static Specification<Room> filterPublicRooms(
            String city,
            String district,
            String nearbyUniversity,
            RoomType roomType,
            BigDecimal minRent,
            BigDecimal maxRent,
            String facility,
            String keyword
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Public search only returns admin-approved, vacant rooms
            predicates.add(cb.equal(root.get("moderationStatus"), ModerationStatus.APPROVED));
            predicates.add(cb.equal(root.get("roomStatus"), RoomStatus.VACANT));

            if (city != null && !city.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), city.trim().toLowerCase()));
            }

            if (district != null && !district.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("district")), district.trim().toLowerCase()));
            }

            if (nearbyUniversity != null && !nearbyUniversity.isBlank()) {
                String pattern = "%" + nearbyUniversity.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("nearbyUniversity")), pattern));
            }

            if (roomType != null) {
                predicates.add(cb.equal(root.get("roomType"), roomType));
            }

            if (minRent != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("monthlyRent"), minRent));
            }

            if (maxRent != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("monthlyRent"), maxRent));
            }

            if (facility != null && !facility.isBlank()) {
                query.distinct(true);
                Join<Room, RoomFacility> facilityJoin = root.join("facilities", JoinType.INNER);
                predicates.add(cb.equal(
                        cb.lower(facilityJoin.get("facilityName")),
                        facility.trim().toLowerCase()
                ));
            }

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("roomTitle")), pattern),
                        cb.like(cb.lower(root.get("roomDescription")), pattern),
                        cb.like(cb.lower(root.get("roomAddress")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
