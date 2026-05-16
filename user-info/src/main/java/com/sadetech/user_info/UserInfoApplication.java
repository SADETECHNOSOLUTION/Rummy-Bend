package com.sadetech.user_info;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = "com.sadetech.user_info", exclude = {org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class})
@EnableMongoRepositories(basePackages = "com.sadetech.user_info.repository")
@EnableDiscoveryClient
@EnableMongoAuditing
// @EnableFeignClients
@EnableScheduling
public class UserInfoApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		SpringApplication.run(UserInfoApplication.class, args);
	}

}