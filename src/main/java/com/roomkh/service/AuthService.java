package com.roomkh.service;

import com.roomkh.dto.request.LoginRequestDTO;
import com.roomkh.dto.request.RegisterRequestDTO;
import com.roomkh.dto.response.AuthResponseDTO;
import com.roomkh.dto.response.UserResponseDTO;

public interface AuthService {
    UserResponseDTO register(RegisterRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
}
