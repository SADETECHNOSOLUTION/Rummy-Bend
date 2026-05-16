package com.sadetech.kyc_verification.service;

import com.sadetech.kyc_verification.exception.PanNumberMismatchException;
import com.sadetech.kyc_verification.model.Kyc;
import com.sadetech.kyc_verification.repository.KycRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class KycService {

    @Autowired
    private KycRepository kycRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private OcrService ocrService;


    private static final Logger logger = LoggerFactory.getLogger(KycService.class);

    public Kyc addKycDetails(boolean kycStatus, String panNumber, MultipartFile panImage, String playerId) {
        String panImagePath = null;

        if (panImage != null && !panImage.isEmpty()) {
            try {
                panImagePath = fileUploadService.uploadFile(panImage);

                // Extract PAN number from the image
                String extractedPanNumber = ocrService.extractTextFromImage(panImagePath);

                // Clean and validate the extracted PAN number
                extractedPanNumber = extractedPanNumber.replaceAll("[^A-Z0-9]", "");

                // Verify if the extracted PAN number matches the user-entered PAN number
                if (!extractedPanNumber.equalsIgnoreCase(panNumber)) {
                    throw new PanNumberMismatchException("PAN number mismatch");
                }
                
            } catch (Exception e) {
                logger.error("Error during KYC processing", e);
                throw new RuntimeException("Error during KYC processing", e);
            }
        }

        Kyc kyc = new Kyc();
        kyc.setKycStatus(kycStatus);
        kyc.setPanNumber(panNumber);
        kyc.setPanImage(panImagePath);
        kyc.setPlayerId(playerId);

        return kycRepository.save(kyc);
    }

    public Kyc getKycDetails(String playerId) {
        return kycRepository.findByPlayerId(playerId);
    }
}
