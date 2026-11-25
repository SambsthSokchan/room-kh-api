package com.roomkh.service;

import com.roomkh.dto.request.BroadcastMessageRequestDTO;
import com.roomkh.dto.request.RoomModerationRequestDTO;
import com.roomkh.dto.request.UserManagementRequestDTO;
import com.roomkh.dto.response.*;

import java.util.List;

public interface AdminService {
    List<RoomResponseDTO> getPendingRooms();
    RoomModerationResponseDTO moderateRoom(Long roomId, Long adminUserId, RoomModerationRequestDTO request);
    UserManagementResponseDTO manageUser(Long targetUserId, Long adminUserId, UserManagementRequestDTO request);
    List<UserManagementResponseDTO> getUserAuditHistory(Long targetUserId);
    BroadcastMessageResponseDTO sendBroadcast(Long adminUserId, BroadcastMessageRequestDTO request);
    List<BroadcastMessageResponseDTO> getBroadcastHistory();
}
