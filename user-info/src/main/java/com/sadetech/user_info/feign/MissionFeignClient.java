package com.sadetech.user_info.feign;

import com.sadetech.user_info.dto.MissionDto;
import com.sadetech.user_info.dto.RewardDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @FeignClient(name = "missions")
public interface MissionFeignClient {

}
