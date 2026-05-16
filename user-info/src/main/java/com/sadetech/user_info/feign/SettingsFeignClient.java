package com.sadetech.user_info.feign;

import com.sadetech.user_info.dto.Settings;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// @FeignClient(name = "settings")
public interface SettingsFeignClient {

}
