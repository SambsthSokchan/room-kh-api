package com.roomkh.service;

import com.roomkh.dto.request.ContractEndRequestDTO;
import com.roomkh.dto.request.ContractRenewalRequestDTO;
import com.roomkh.dto.request.RentalContractRequestDTO;
import com.roomkh.dto.response.RentalContractResponseDTO;

import java.util.List;

public interface RentalContractService {
    RentalContractResponseDTO createContract(Long landlordUserId, RentalContractRequestDTO request);
    List<RentalContractResponseDTO> getMyContracts(Long renterUserId);
    List<RentalContractResponseDTO> getLandlordContracts(Long landlordUserId);
    RentalContractResponseDTO closeContract(Long contractId, Long landlordUserId, ContractEndRequestDTO request);
    RentalContractResponseDTO renewContract(Long contractId, Long landlordUserId, ContractRenewalRequestDTO request);
}
