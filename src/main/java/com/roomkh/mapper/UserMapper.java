package com.roomkh.mapper;

import com.roomkh.dto.response.UserResponseDTO;
import com.roomkh.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDTO toResponse(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .userSegment(user.getUserSegment())
                .language(user.getLanguage())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
