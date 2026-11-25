package com.roomkh.repository;

import com.roomkh.entity.LandlordProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LandlordProfileRepository extends JpaRepository<LandlordProfile, Long> {
    Optional<LandlordProfile> findByUser_UserId(Long userId);
    boolean existsByUser_UserId(Long userId);
}
