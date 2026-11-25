package com.roomkh.controller;

import com.roomkh.dto.response.ApiResponseDTO;
import com.roomkh.dto.response.UserResponseDTO;
import com.roomkh.mapper.UserMapper;
import com.roomkh.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                ApiResponseDTO.ok("Profile loaded", userMapper.toResponse(userDetails.getUser()))
        );
    }
}
