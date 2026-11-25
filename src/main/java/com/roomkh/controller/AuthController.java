package com.roomkh.controller;

import com.roomkh.dto.request.LoginRequestDTO;
import com.roomkh.dto.request.RegisterRequestDTO;
import com.roomkh.dto.response.ApiResponseDTO;
import com.roomkh.dto.response.AuthResponseDTO;
import com.roomkh.dto.response.UserResponseDTO;
import com.roomkh.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO request
    ) {
        UserResponseDTO user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Registration successful", user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request
    ) {
        AuthResponseDTO auth = authService.login(request);
        return ResponseEntity.ok(ApiResponseDTO.ok("Login successful", auth));
    }
}
