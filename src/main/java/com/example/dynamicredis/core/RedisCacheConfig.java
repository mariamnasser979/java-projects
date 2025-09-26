package com.example.dynamicredis.core;

public class RedisCacheConfig {
    public String name;
    public String host;
    public int port;
    public int db;
    public String password;
    public int timeoutMs = 2000;
    public int ttlSeconds = 3600;
}