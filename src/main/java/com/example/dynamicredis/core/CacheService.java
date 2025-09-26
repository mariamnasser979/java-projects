package com.example.dynamicredis.core;

import com.example.dynamicredis.config.ConfigLoader;
import com.example.dynamicredis.config.RedisClientManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

public class CacheService {
    private final RedisClientManager manager;
    private final ConfigLoader loader;

    public CacheService(RedisClientManager manager, ConfigLoader loader) {
        this.manager = manager;
        this.loader = loader;
    }

    private RedisTemplate<byte[], byte[]> template(RedisConnectionFactory f) {
        RedisTemplate<byte[], byte[]> t = new RedisTemplate<>();
        t.setConnectionFactory(f);
        t.afterPropertiesSet();
        return t;
    }

    public void put(String cacheName, String key, String value) throws Exception {
        RedisConnectionFactory f = manager.getFactory(cacheName);
        RedisTemplate<byte[], byte[]> t = template(f);
        Properties p = loader.load(cacheName);
        int ttl = Integer.parseInt(p.getProperty("redis.ttl.seconds", "3600"));
        t.opsForValue().set(key.getBytes(StandardCharsets.UTF_8),
                value.getBytes(StandardCharsets.UTF_8),
                Duration.ofSeconds(ttl));
    }

    public String get(String cacheName, String key) throws Exception {
        RedisConnectionFactory f = manager.getFactory(cacheName);
        RedisTemplate<byte[], byte[]> t = template(f);
        byte[] v = t.opsForValue().get(key.getBytes(StandardCharsets.UTF_8));
        return v == null ? null : new String(v, StandardCharsets.UTF_8);
    }
}