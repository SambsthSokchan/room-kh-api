package com.roomkh.repository;

import com.roomkh.entity.UserManagement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserManagementRepository extends JpaRepository<UserManagement, Long> {
    List<UserManagement> findByTargetUser_UserIdOrderByCreatedAtDesc(Long targetUserId);
}
