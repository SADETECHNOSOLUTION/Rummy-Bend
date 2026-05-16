package com.sadetech.missions.controller;

import com.sadetech.missions.model.Mission;
import com.sadetech.missions.model.ReferAndEarnAmount;
import com.sadetech.missions.model.Rewards;
import com.sadetech.missions.service.MissionService;
import com.sadetech.missions.service.ReferAndEarnService;
import com.sadetech.missions.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mission")
public class MissionController {

    @Autowired
    private MissionService missionService;

    @Autowired
    private RewardService rewardService;

    @Autowired
    private ReferAndEarnService referAndEarnService;

    @PostMapping("/post-mission")
    public ResponseEntity<Mission> addMissionToPlayer(@RequestBody Mission mission){
        Mission mission1 = missionService.createMission(mission);
        return ResponseEntity.status(HttpStatus.CREATED).body(mission1);
    }

    @PostMapping("/create-rewards")
    public ResponseEntity<Rewards> createDailyRewards(@RequestBody Rewards rewards){
        Rewards rewards1 = rewardService.createRewards(rewards);
        return ResponseEntity.status(HttpStatus.CREATED).body(rewards1);
    }

    @GetMapping("/get-all-mission")
    public ResponseEntity<List<Mission>> getAllMission(){
        List<Mission> getAllMission = missionService.getAllMission();
        return ResponseEntity.status(HttpStatus.OK).body(getAllMission);
    }

    @GetMapping("/get-mission/{playerId}")
    public ResponseEntity<List<Mission>> getAllMissionByPlayerId(@PathVariable String playerId){
        List<Mission> getAllMission = missionService.getMissionByPlayerId(playerId);
        return ResponseEntity.status(HttpStatus.OK).body(getAllMission);
    }

    @GetMapping("/get-daily-challenges/{playerId}")
    public ResponseEntity<List<Mission>> getAllDailyChallengesByPlayerId(@PathVariable String playerId){
        List<Mission> getAllDailyChallenges = missionService.getDailyChallengesByPlayerId(playerId);
        return ResponseEntity.status(HttpStatus.OK).body(getAllDailyChallenges);
    }

    @GetMapping("/get-all-rewards")
    public ResponseEntity<List<Rewards>> getAllRewards(){
        List<Rewards> rewards = rewardService.getAllRewards();
        return ResponseEntity.status(HttpStatus.OK).body(rewards);
    }

    @GetMapping("/get-rewards/player/{playerId}")
    public ResponseEntity<List<Rewards>> getAllRewardsByPlayerId(@PathVariable String playerId){
        List<Rewards> rewardsList = rewardService.getFilteredRewards(playerId);
        return ResponseEntity.status(HttpStatus.OK).body(rewardsList);
    }

    @GetMapping("/unassigned")
    public List<Mission> getUnassignedMissions() {
        return missionService.findByPlayerIdIsNull();
    }

    @GetMapping("/unassigned-rewards")
    public List<Rewards> getUnassignedRewards() {
        return rewardService.findByPlayerIdIsNull();
    }

    @PutMapping("/update-mission-status/{id}")
    public ResponseEntity<Mission> updateMission(@PathVariable String id){
        Mission mission = missionService.updateMission(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(mission);
    }

    @GetMapping("/get-mission-by-id/{id}")
    public ResponseEntity<Mission> getMission(@PathVariable String id){
        Mission mission = missionService.getMissionById(id);
        return ResponseEntity.status(HttpStatus.OK).body(mission);
    }

    @GetMapping("/get-rewards/{id}")
    public ResponseEntity<Rewards> getRewards(@PathVariable String id){
        Rewards rewards = rewardService.getRewardsById(id);
        return ResponseEntity.status(HttpStatus.OK).body(rewards);
    }

    @GetMapping("/get-all-daily-challenges")
    public ResponseEntity<List<Mission>> getAllDailyChallenges(){
        List<Mission> mission = missionService.getAllDailyChallenge();
        return ResponseEntity.status(HttpStatus.OK).body(mission);
    }

    @PutMapping("/update-daily-challenge/{id}")
    public ResponseEntity<Mission> updateDailyChallenge(@PathVariable String id){
        Mission mission = missionService.updateDailyChallenge(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(mission);
    }

    @GetMapping("/get-mission-player/{playerId}")
    public ResponseEntity<List<Mission>> getMissionForPlayers(@PathVariable String playerId){
        List<Mission> missionList = missionService.getAllMissionForPlayer(playerId);
        return ResponseEntity.status(HttpStatus.OK).body(missionList);
    }

    @PostMapping("/post-rank")
    public ResponseEntity<String> addRankForReferAndEarn(@RequestBody ReferAndEarnAmount referAndEarnAmount){
        String response = referAndEarnService.postReferAndEarnAmount(referAndEarnAmount);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/get-rank")
    public ResponseEntity<ReferAndEarnAmount> getRankDetails(@RequestParam int rank){
        ReferAndEarnAmount referAndEarnAmount = referAndEarnService.getAmount(rank);
        return ResponseEntity.status(HttpStatus.OK).body(referAndEarnAmount);
    }

    @GetMapping("/get-all-rank")
    public ResponseEntity<List<ReferAndEarnAmount>> getAllRankDetails(){
        List<ReferAndEarnAmount> referAndEarnAmounts = referAndEarnService.getAllRank();
        return ResponseEntity.status(HttpStatus.OK).body(referAndEarnAmounts);
    }

}
