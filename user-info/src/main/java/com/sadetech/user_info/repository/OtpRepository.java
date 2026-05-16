package com.sadetech.user_info.repository;
import com.sadetech.user_info.model.Otp;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface OtpRepository extends MongoRepository<Otp, String> {
    Optional<Otp> findByEmailAndOtp(String email, String otp);

    Optional<Otp> findFirstByPhoneNumberAndUsedFalseOrderByCreatedAtDesc(String phoneNumber);

    Optional<Otp> findByPhoneNumberAndOtp(String email, String otp);

    Optional<Otp> findFirstByEmailOrPhoneNumberAndUsedFalseOrderByCreatedAtDesc(String input, String input1);

    Optional<Otp> findFirstByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);

    Optional<Otp> findFirstByEmailOrPhoneNumberOrderByCreatedAtDesc(String input, String input1);
}



