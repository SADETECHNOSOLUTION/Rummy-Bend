package com.sadetech.kyc_verification.service;

import jakarta.annotation.PostConstruct;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class OcrService {

    private final Tesseract tesseract;

    @Value("${tesseract.datapath}")
    private String tessDataPath;

    public OcrService() {
        tesseract = new Tesseract();
        // Set the path to the Tesseract native library
        System.setProperty("jna.library.path", "/usr/lib/x86_64-linux-gnu");
    }

    @PostConstruct
    public void init() {
        File tessdataFolder = new File(tessDataPath);
        if (!tessdataFolder.exists() || !tessdataFolder.isDirectory()) {
            throw new RuntimeException("Tessdata folder not found: " + tessDataPath);
        }
        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage("eng");
    }

    public String extractTextFromImage(String imagePath) throws TesseractException {
        File imageFile = new File(imagePath);
        return tesseract.doOCR(imageFile);
    }
}