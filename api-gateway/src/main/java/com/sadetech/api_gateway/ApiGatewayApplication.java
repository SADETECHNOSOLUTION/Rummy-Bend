package com.sadetech.api_gateway;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching

public class ApiGatewayApplication {
@Value("${spring.data.redis.url:NO_URL_FOUND}")
private String redisUrl;

@PostConstruct
public void init() {
        System.out.println("==========================================");
        System.out.println("GATEWAY CONNECTING TO REDIS AT: " + redisUrl);
        System.out.println("SYSTEM ENV REDIS_URL: " + System.getenv("REDIS_URL"));
        System.out.println("==========================================");
    }

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
        .ignoreIfMissing() // This prevents the crash on Render
        .load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
