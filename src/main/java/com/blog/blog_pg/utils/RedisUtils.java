package com.blog.blog_pg.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.stereotype.Component;

@Component
public class RedisUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final StatefulRedisConnection<String, String> redisConnection;
    private final RedisCommands<String, String> redisCache;

    public RedisUtils(StatefulRedisConnection<String, String> redisConnection) {
        if (redisConnection == null) {
            throw new IllegalArgumentException("Redis connection cannot be null");
        }
        this.redisConnection = redisConnection;
        this.redisCache = redisConnection.sync();
    }

    public String setCacheIO(String key, Object value) throws Exception {
        if (redisCache == null) {
            throw new IllegalStateException("Redis client not initialized");
        }
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            return redisCache.set(key, jsonValue);
        } catch (Exception e) {
            throw new Exception("Failed to set cache: " + e.getMessage(), e);
        }
    }

    public String setCacheIOExpiration(String key, Object value, int expirationInSeconds) throws Exception {
        if (redisCache == null) {
            throw new IllegalStateException("Redis client not initialized");
        }
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            return redisCache.setex(key, expirationInSeconds, jsonValue);
        } catch (Exception e) {
            throw new Exception("Failed to set cache with expiration: " + e.getMessage(), e);
        }
    }

    public <T> T getCacheIO(String key, Class<T> clazz) throws Exception {
        if (redisCache == null) {
            throw new IllegalStateException("Redis client not initialized");
        }
        try {
            String result = redisCache.get(key);
            if (result == null) {
                return null;
            }
            return objectMapper.readValue(result, clazz);
        } catch (Exception e) {
            throw new Exception("Failed to get cache: " + e.getMessage(), e);
        }
    }

    public Long deleteCacheIO(String key) throws Exception {
        if (redisCache == null) {
            throw new IllegalStateException("Redis client not initialized");
        }
        try {
            return redisCache.del(key);
        } catch (Exception e) {
            throw new Exception("Failed to delete cache: " + e.getMessage(), e);
        }
    }

    // Singleton getter
    public static RedisUtils getInstance(StatefulRedisConnection<String, String> redisConnection) {
        return new RedisUtils(redisConnection);
    }
}