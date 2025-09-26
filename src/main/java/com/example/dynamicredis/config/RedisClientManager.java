package com.example.dynamicredis.config;

import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.*;
import io.lettuce.core.resource.DefaultClientResources;

import java.time.*;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class RedisClientManager {
    private final ConfigLoader loader;
    private final ConcurrentHashMap<String, LettuceConnectionFactory> factories = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> lastLoaded = new ConcurrentHashMap<>();
    private final DefaultClientResources clientResources = DefaultClientResources.create();
    private final long reloadEverySeconds = 60;

    public RedisClientManager(ConfigLoader loader) {
        this.loader = loader;
    }

    public RedisConnectionFactory getFactory(String cacheName) {
        Instant now = Instant.now();
        Instant prev = lastLoaded.get(cacheName);
        if (prev == null || Duration.between(prev, now).getSeconds() > reloadEverySeconds) {
            return factories.compute(cacheName, (k, existing) -> {
                try {
                    Properties p = loader.load(cacheName);
                    if (existing != null) existing.destroy();

                    RedisStandaloneConfiguration server = new RedisStandaloneConfiguration(
                            p.getProperty("redis.host"),
                            Integer.parseInt(p.getProperty("redis.port", "6379"))
                    );
                    server.setDatabase(Integer.parseInt(p.getProperty("redis.db", "0")));
                    String pwd = p.getProperty("redis.password", "");
                    if (!pwd.isBlank()) server.setPassword(RedisPassword.of(pwd));

                    LettuceClientConfiguration clientCfg = LettuceClientConfiguration.builder()
                            .clientResources(clientResources)
                            .commandTimeout(Duration.ofMillis(
                                    Integer.parseInt(p.getProperty("redis.timeout.ms", "2000"))
                            ))
                            .build();

                    LettuceConnectionFactory f = new LettuceConnectionFactory(server, clientCfg);
                    f.afterPropertiesSet();
                    lastLoaded.put(cacheName, now);
                    return f;
                } catch (Exception e) {
                    if (existing != null) return existing;
                    throw new RuntimeException("Failed to load Redis factory for " + cacheName, e);
                }
            });
        }
        return factories.get(cacheName);
    }
}
