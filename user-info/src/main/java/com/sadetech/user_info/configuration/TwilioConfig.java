package com.sadetech.user_info.configuration;

import com.sadetech.user_info.service.UsersManagementService;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

public class TwilioConfig {

  @Autowired
  private UsersManagementService usersManagementService;
  // Find your Account Sid and Token at twilio.com/console
  public static final String ACCOUNT_SID = "ACf8d38b1412cfac187d9f5e0ac303e863";
  public static final String AUTH_TOKEN = "[AuthToken]";

  private static String generateOtp() {
    Random random = new Random();
    int otp = 100000 + random.nextInt(900000);
    return String.valueOf(otp);
  }


  public static void main(String[] args) {



    String otp = generateOtp();


    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    Verification verification = Verification.creator(
            "VAc8d4924c790a53689ad21f9c1806309f",
            "+919655493552",
            "Your otp is : " + otp )
        .create();

    System.out.println(verification.getSid());
  }
}