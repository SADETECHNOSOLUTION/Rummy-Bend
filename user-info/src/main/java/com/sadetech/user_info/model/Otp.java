    package com.sadetech.user_info.model;

    import lombok.*;
    import org.springframework.data.annotation.CreatedDate;
    import org.springframework.data.annotation.Id;
    import org.springframework.data.mongodb.core.mapping.Document;

    import java.time.LocalDateTime;

    @Data
    @Builder // Added this for easier object creation
    @AllArgsConstructor
    @NoArgsConstructor
    @Document(collection = "otp_list")
    public class Otp {

        @Id
        private String id;
        private String email;
        private String phoneNumber;
        private String otp;
        private String otpType;
        private String emailOtpContent;
        @CreatedDate
        private LocalDateTime createdAt;

        private boolean used = false;

    }
