package com.example.dynamicredis.config;

import com.example.dynamicredis.core.CacheService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisDynamicConfig {

    @Bean
    public ConfigLoader configLoader(@Value("${app.redis.config.base}") String base) {
        return new ConfigLoader(base);
    }

    @Bean
    public RedisClientManager redisClientManager(ConfigLoader loader) {
        return new RedisClientManager(loader);
    }

    @Bean
    public CacheService cacheService(RedisClientManager manager, ConfigLoader loader) {
        return new CacheService(manager, loader);
    }
}
