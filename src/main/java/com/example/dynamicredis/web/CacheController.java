package com.example.dynamicredis.web;

import com.example.dynamicredis.config.ConfigLoader;
import com.example.dynamicredis.core.CacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final CacheService cacheService;
    private final ConfigLoader loader;

    public CacheController(CacheService cacheService, ConfigLoader loader) {
        this.cacheService = cacheService;
        this.loader = loader;
    }

    @GetMapping("/config/{cacheName}")
    public ResponseEntity<Map<String, String>> showConfig(@PathVariable String cacheName) throws IOException {
        Properties p = loader.load(cacheName);
        Map<String, String> map = p.entrySet().stream()
                .collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue())));
        return ResponseEntity.ok(map);
    }

    @PostMapping("/{cacheName}/put")
    public ResponseEntity<Map<String, String>> put(@PathVariable String cacheName,
                                                   @RequestParam String key,
                                                   @RequestParam String value) throws Exception {
        cacheService.put(cacheName, key, value);
        return ResponseEntity.ok(Map.of("status", "OK", "cache", cacheName, "key", key, "value", value));
    }

    @GetMapping("/{cacheName}/get")
    public ResponseEntity<Map<String, String>> get(@PathVariable String cacheName,
                                                   @RequestParam String key) throws Exception {
        String v = cacheService.get(cacheName, key);
        return ResponseEntity.ok(Map.of("cache", cacheName, "key", key, "value", v == null ? "" : v));
    }
}