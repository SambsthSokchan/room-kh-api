package com.roomkh.service;

import com.roomkh.dto.response.DashboardResponseDTO;
import com.roomkh.dto.response.LandlordDashboardResponseDTO;
import com.roomkh.dto.response.UserDashboardResponseDTO;

public interface DashboardService {
    DashboardResponseDTO getAdminDashboard();
    LandlordDashboardResponseDTO getLandlordDashboard(Long userId);
    UserDashboardResponseDTO getUserDashboard(Long userId);
}

