package com.sadetech.game_engine.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class TTLIndexConfig {

    @Bean
    CommandLineRunner createTTLIndex(MongoTemplate mongoTemplate) {
        return args -> {
            mongoTemplate.indexOps("card_update_logs").ensureIndex(
                new org.springframework.data.mongodb.core.index.Index()
                    .on("timestamp", org.springframework.data.domain.Sort.Direction.ASC)
                    .expire(15L * 24 * 60 * 60) // 15 days in seconds
            );
        };
    }
}
