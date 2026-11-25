package com.roomkh.controller;

import com.roomkh.dto.request.ContractEndRequestDTO;
import com.roomkh.dto.request.ContractRenewalRequestDTO;
import com.roomkh.dto.request.RentalContractRequestDTO;
import com.roomkh.dto.response.ApiResponseDTO;
import com.roomkh.dto.response.RentalContractResponseDTO;
import com.roomkh.security.CustomUserDetails;
import com.roomkh.service.RentalContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/contracts", "/api/rental-contracts"})
@RequiredArgsConstructor
public class RentalContractController {

    private final RentalContractService contractService;

    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponseDTO<RentalContractResponseDTO>> createContract(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RentalContractRequestDTO request
    ) {
        RentalContractResponseDTO created = contractService.createContract(
                userDetails.getUser().getUserId(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.ok("Rental contract created", created));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<ApiResponseDTO<List<RentalContractResponseDTO>>> getMyContracts(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Your rental contracts loaded",
                contractService.getMyContracts(userDetails.getUser().getUserId())
        ));
    }

    @GetMapping("/landlord")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponseDTO<List<RentalContractResponseDTO>>> getLandlordContracts(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponseDTO.ok(
                "Landlord contracts loaded",
                contractService.getLandlordContracts(userDetails.getUser().getUserId())
        ));
    }

    @PatchMapping("/{contractId}/close")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponseDTO<RentalContractResponseDTO>> closeContract(
            @PathVariable Long contractId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ContractEndRequestDTO request
    ) {
        RentalContractResponseDTO updated = contractService.closeContract(
                contractId,
                userDetails.getUser().getUserId(),
                request
        );
        return ResponseEntity.ok(ApiResponseDTO.ok("Rental contract closed", updated));
    }

    @PatchMapping("/{contractId}/renew")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ApiResponseDTO<RentalContractResponseDTO>> renewContract(
            @PathVariable Long contractId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ContractRenewalRequestDTO request
    ) {
        RentalContractResponseDTO renewed = contractService.renewContract(
                contractId,
                userDetails.getUser().getUserId(),
                request
        );
        return ResponseEntity.ok(ApiResponseDTO.ok("Rental contract renewed", renewed));
    }
}
