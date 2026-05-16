package com.sadetech.user_info.service;

import com.sadetech.user_info.model.Otp;
import com.sadetech.user_info.repository.OtpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private OtpRepository otpRepository;

    private final JavaMailSender javaMailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendOtpEmail(String to, String otp, String otpType) {
        if (to == null || otp == null || otpType == null) {
            throw new IllegalArgumentException("Recipient email, OTP, and OTP type must not be null.");
        }

        String subject;
        String text = switch (otpType) {
            case "Sign up" -> {
                subject = "Your OTP Code for Registration";
                yield "Your OTP for registration is: " + otp;
            }
            case "Reset-Password" -> {
                subject = "Rummy Queen - OTP for Password Reset";
                yield String.format("""
                Dear User,
               \s
                It seems you forgot your password. Don't worry! We are here to help.
               \s
                Your OTP for resetting your password is: %s
               \s
                Enter this OTP in the Rummy Queen app to reset your password.
               \s
                For any further assistance, you can always refer to our self-help guide on our mobile app by clicking on "Menu" followed by the "Help" icon.
               \s
                See you at the rummy tables!
               \s
                Team Rummy Queen
                The Most Trusted Rummy App
           \s""", otp);
            }
            case "Change-Email" -> {
                subject = "Your OTP Code for Changing Email";
                yield "Your OTP for changing your email is: " + otp;
            }
            default -> throw new IllegalArgumentException("Unsupported OTP type: " + otpType);
        };

        // Save email content to the database
        Otp otpEntity = Otp.builder()
    .email(to)
    .otp(otp)
    .otpType(otpType)
    .emailOtpContent(text)
    .createdAt(LocalDateTime.now())
    .used(false)
    .build();
        otpRepository.save(otpEntity);

        // Send the email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            logger.info("Attempting to send email to: {}", to);
            javaMailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
        } catch (MailException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

}
