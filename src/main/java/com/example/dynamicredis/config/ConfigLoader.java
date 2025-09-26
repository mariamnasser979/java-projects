package com.example.dynamicredis.config;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class ConfigLoader {
    private final Path baseDir;

    public ConfigLoader(String baseDir) {
        this.baseDir = Paths.get(baseDir).toAbsolutePath().normalize();
    }

    public Properties load(String cacheName) throws IOException {
        if (!cacheName.matches("[a-zA-Z0-9_-]+"))
            throw new IllegalArgumentException("Invalid cache name");

        Path file = baseDir.resolve(cacheName + ".properties").normalize();
        if (!file.startsWith(baseDir))
            throw new SecurityException("Path traversal detected");
        if (!Files.exists(file))
            throw new FileNotFoundException("Config not found for " + cacheName);

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            props.load(in);
        }
        return props;
    }
}