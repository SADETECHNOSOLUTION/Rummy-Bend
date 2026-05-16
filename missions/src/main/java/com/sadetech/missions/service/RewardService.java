package com.sadetech.missions.service;

import com.sadetech.missions.dto.User;
import com.sadetech.missions.exception.EventNotFoundException;
import com.sadetech.missions.exception.ResourceNotFoundException;
import com.sadetech.missions.feign.UserFeignClient;
import com.sadetech.missions.model.Rewards;
import com.sadetech.missions.repository.RewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RewardService {

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private UserFeignClient userFeignClient;

    /**
     * Creates a new reward entry in the database.
     */
    public Rewards createRewards(Rewards rewards) {
        return rewardRepository.save(rewards);
    }

    /**
     * Fetches all rewards from the database.
     */
    public List<Rewards> getAllRewards() {
        List<Rewards> rewards = rewardRepository.findAll();
        if (rewards.isEmpty()) {
            throw new ResourceNotFoundException("No data found");
        }
        return rewards;
    }

    /**
     * Fetches and filters rewards based on user deposit amount and eligibility.
     */
    public List<Rewards> getFilteredRewards(String playerId) {

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Input should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        // Fetch user details via Feign Client
        Optional<User> userOptional = userFeignClient.getDetails(playerId);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found for playerId: " + playerId);
        }

        User user = userOptional.get();
        double totalDepositMoney = user.getTotalDepositMoney();

        // Fetch rewards from MongoDB based on player deposit eligibility
        List<Rewards> allRewards = rewardRepository.findRewardsByPlayerId(playerId, totalDepositMoney);

        // Apply filtering logic based on depositMoney and current day
        return allRewards.stream()
                .filter(reward -> {
                    int stage = getStageBasedOnDeposit(user);
                    return reward.getHeading().contains("Stage " + stage);
                })
                .collect(Collectors.toList());
    }

    /**
     * Determines the reward stage based on the player's total deposit amount.
     */
    private int getStageBasedOnDeposit(User user) {
        double totalDepositMoney = user.getTotalDepositMoney();
        boolean isWalletRecharge = user.isWalletRecharge();
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        if (!isWalletRecharge) {
            return 1; // Show only stage 1
        }
        if (totalDepositMoney > 5000) {
            return 3; // Show stage 3
        }
        if (totalDepositMoney > 2500) {
            return 2; // Show stage 2
        }
        if (today == DayOfWeek.SATURDAY || today == DayOfWeek.SUNDAY) {
            return 5; // Show stage 5 on weekends
        }
        return 4; // Show stage 4 daily
    }

    /**
     * Fetches rewards where playerId is null.
     */
    public List<Rewards> findByPlayerIdIsNull() {
        return rewardRepository.findByPlayerIdIsNull();
    }

    /**
     * Fetches a reward by ID.
     */
    public Rewards getRewardsById(String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Input should not be empty.");
        }

        id = id.trim().replaceAll("\\s+", "");

        if (!id.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("ID must be a valid 24-character hexadecimal string.");
        }

        return rewardRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("No mission found for the mission id." ));
    }

    /**
     * **Scheduled Task for Monitoring Stage 4**
     * Runs **daily at midnight (00:00)** to check and regenerate **Stage 4 rewards**.
     */
    @Scheduled(cron = "0 0 0 * * ?")  // Runs at 12:00 AM daily
    public void monitorStage4() {
        processRewards("Stage 4");
    }

    /**
     * **Scheduled Task for Monitoring Stage 5**
     * Runs **only on Saturdays and Sundays at midnight (00:00)** to check and regenerate **Stage 5 rewards**.
     */
    @Scheduled(cron = "0 0 0 * * SAT,SUN")  // Runs at 12:00 AM on weekends
    public void monitorStage5() {
        processRewards("Stage 5");
    }

    /**
     * Checks rewards for a specific stage and regenerates them if they are marked as "Redeemed".
     */
    private void processRewards(String stage) {
        List<Rewards> redeemedRewards = rewardRepository.findByStageAndProgress(stage, "Redeemed");

        for (Rewards reward : redeemedRewards) {
            // Create a new reward with a new unique ID and progress reset to "Redeem now"
            Rewards newReward = new Rewards(
                    null,
                    reward.getHeading(),
                    reward.getStage(),
                    reward.getTask(),
                    reward.getCoupon(),
                    reward.getCreatedAt(),
                    reward.getDepositMoney(),
                    reward.getBonusLimit(),
                    reward.getBonusPercentage(),
                    reward.getRemarks(),
                    "Redeem now", // Reset progress
                    reward.getPlayerId()
            );

            // Save the new reward in the database
            rewardRepository.save(newReward);
        }
    }
}
