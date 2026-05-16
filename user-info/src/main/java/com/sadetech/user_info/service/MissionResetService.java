package com.sadetech.user_info.service;

import com.sadetech.user_info.dto.MissionDto;
import com.sadetech.user_info.feign.MissionFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MissionResetService {

    // private final MissionFeignClient missionFeignClient;

    // public MissionResetService(MissionFeignClient missionFeignClient) {
    //     this.missionFeignClient = missionFeignClient;
    // }

    private static final Logger logger = LoggerFactory.getLogger(MissionResetService.class);

    // @Scheduled(cron = "0 0 0 * * ?")
    public void resetDailyChallenges() {
        // logger.info("🔄 Resetting daily challenges for all players...");

        // // Fetch all daily challenge missions from the Mission Service
        // List<MissionDto> dailyChallenges = missionFeignClient.getAllDailyChallenges();

        // if (dailyChallenges == null || dailyChallenges.isEmpty()) {
        //     logger.info("✅ No daily challenges found for reset. Skipping...");
        //     return;
        // }

        // // Update each daily challenge
        // for (MissionDto mission : dailyChallenges) {
        //     missionFeignClient.updateDailyChallenge(mission.getId());
        // }

        // logger.info("✅ All daily challenges have been reset successfully.");
    }

}