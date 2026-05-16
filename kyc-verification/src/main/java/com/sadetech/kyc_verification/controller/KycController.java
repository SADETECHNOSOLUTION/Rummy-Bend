package com.sadetech.kyc_verification.controller;

import com.sadetech.kyc_verification.exception.PanNumberMismatchException;
import com.sadetech.kyc_verification.model.Kyc;
import com.sadetech.kyc_verification.service.KycService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/kyc")
public class KycController {

    @Autowired
    private KycService kycService;

    @PostMapping("/add-kyc")
    public ResponseEntity<?> addKyc(
            @RequestParam(required = false, defaultValue = "Pending") String kycStatus,
            @RequestParam(required = false, defaultValue = "Unknown") String panNumber,
            @RequestParam(required = false) MultipartFile panImage,
            @RequestParam String playerId
            ){
        try{
            Kyc kyc = kycService.addKycDetails(kycStatus,panNumber,panImage,playerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(kyc);
        }catch (PanNumberMismatchException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during KYC processing");
        }
    }

    @GetMapping("/get-kyc/{playerId}")
    public Kyc getKycDetails(@PathVariable String playerId){
        return kycService.getKycDetails(playerId);
    }
}
