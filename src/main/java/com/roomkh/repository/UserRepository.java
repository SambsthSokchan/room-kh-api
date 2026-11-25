package com.roomkh.repository;

import com.roomkh.entity.User;
import com.roomkh.enums.UserRole;
import com.roomkh.enums.UserSegment;
import com.roomkh.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    List<User> findByStatus(UserStatus status);
    List<User> findByUserSegmentAndStatus(UserSegment userSegment, UserStatus status);
    List<User> findByRoleAndStatus(UserRole role, UserStatus status);
    long countByStatus(UserStatus status);
    long countByRoleAndStatus(UserRole role, UserStatus status);
    long countByUserSegmentAndStatus(UserSegment userSegment, UserStatus status);
}
