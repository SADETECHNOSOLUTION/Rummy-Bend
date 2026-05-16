package com.sadetech.missions.service;

import com.sadetech.missions.dto.User;
import com.sadetech.missions.exception.EventNotFoundException;
import com.sadetech.missions.exception.PlayerNotFoundException;
import com.sadetech.missions.exception.ResourceNotFoundException;
import com.sadetech.missions.feign.UserFeignClient;
import com.sadetech.missions.model.Mission;
import com.sadetech.missions.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MissionService {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserFeignClient userFeignClient;

    public Mission createMission(Mission mission){
       return mongoTemplate.save(mission);
    }

    public List<Mission> getAllMission(){
        List<Mission> missions = mongoTemplate.findAll(Mission.class);
        if(missions.isEmpty()){
            throw new ResourceNotFoundException("No data found");
        }

        return missions;
    }

    public List<Mission> getMissionByPlayerId(String playerId) {

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Input should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        User user = userFeignClient.getDetails(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("No data found for the player id "));

        List<Mission> missions = missionRepository.findByPlayerId(user.getPlayerId());

        if (missions.isEmpty()) {
            throw new EventNotFoundException("No data found");
        }

        // Step 1: Group missions based on their visibility (e.g., "Group 1", "Group 2")
        Map<String, List<Mission>> groupedMissions = missions.stream()
                .filter(mission -> mission.getType().equals("Mission"))
                .collect(Collectors.groupingBy(Mission::getVisibility));

        // Step 2: Sort groups by their number (Group 1 -> Group 2 -> Group 3 ...)
        List<String> sortedGroups = groupedMissions.keySet().stream()
                .sorted(Comparator.comparingInt(group -> Integer.parseInt(group.split(" ")[1]))) // Extract number and sort
                .toList();

        // Step 3: Find the first incomplete group
        for (String group : sortedGroups) {
            List<Mission> groupMissions = groupedMissions.get(group);

            boolean allCompleted = groupMissions.stream()
                    .allMatch(mission -> "Completed".equalsIgnoreCase(mission.getProgress()));

            if (!allCompleted) {
                // Return only this group's missions
                return groupMissions;
            }
        }

        return new ArrayList<>(); // No missions if all are completed
    }

    public List<Mission> getDailyChallengesByPlayerId(String playerId){

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Input should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        User user = userFeignClient.getDetails(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("No data found for the player id. "));

        List<Mission> missions = missionRepository.findByPlayerId(user.getPlayerId());

        if(missions.isEmpty()){
            throw new EventNotFoundException("No event found for the player id");
        }

        return missions.stream()
                .filter(mission -> mission.getType().equals("Daily Challenges"))
                .toList();
    }


    public List<Mission> findByPlayerIdIsNull() {
        return missionRepository.findByPlayerIdIsNull();
    }

    public Mission updateMission(String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Input should not be empty.");
        }

        id = id.trim().replaceAll("\\s+", "");

        if (!id.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("ID must be a valid 24-character hexadecimal string.");
        }

        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Mission not found"));

        int totalRound = mission.getTotalRound();
        if (totalRound <= 0) {
            throw new IllegalStateException("Total round is not properly defined for mission ID: " + id);
        }

        // Increment round
        mission.setRound(mission.getRound() + 1);

        // Check if mission is completed
        if (mission.getRound() >= mission.getTotalRound()) {
            mission.setProgress("Completed");
        }

        return missionRepository.save(mission);
    }

    public Mission getMissionById(String id){

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Input should not be empty.");
        }

        id = id.trim().replaceAll("\\s+", "");

        if (!id.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("ID must be a valid 24-character hexadecimal string.");
        }

        return missionRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("No mission found for the mission id "));
    }


    public List<Mission> getAllDailyChallenge() {
        List<Mission> missions =  missionRepository.findByType("Daily Challenges");

        if(missions.isEmpty()){
            throw new EventNotFoundException("No daily challenges found");
        }

        return missions;
    }


    public Mission updateDailyChallenge(String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Input should not be empty.");
        }

        id = id.trim().replaceAll("\\s+", "");

        if (!id.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("ID must be a valid 24-character hexadecimal string.");
        }

        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Mission not found"));

        // Reset mission progress and round
        mission.setProgress("Not yet started");
        mission.setRound(0);

        return missionRepository.save(mission); // ✅ Save and return updated mission
    }

    public List<Mission> getAllMissionForPlayer(String playerId){

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Input should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        List<Mission> missionList = missionRepository.findByPlayerId(playerId);

        if(missionList.isEmpty()){
            throw new EventNotFoundException("No mission found for the player id");
        }

        return missionList;
    }

}
