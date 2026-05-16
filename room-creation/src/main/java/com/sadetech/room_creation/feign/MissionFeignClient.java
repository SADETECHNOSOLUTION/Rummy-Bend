package com.sadetech.room_creation.feign;

import com.sadetech.room_creation.dto.MissionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(name = "missions")
public interface MissionFeignClient {

    @GetMapping("/api/mission/get-mission/{playerId}")
    List<MissionDto> getAllMissionByPlayerId(@PathVariable String playerId);

    @PutMapping("/api/mission/update-mission-status/{id}")
    MissionDto updateMission(String id);

    @GetMapping("/api/mission/get-daily-challenges/{playerId}")
    List<MissionDto> getAllDailyChallengesByPlayerId(@PathVariable String playerId);


}
