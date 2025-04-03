package com.blog.blog_pg.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;

@Configuration
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    private static final String REDIS_URL = "redis://myusername:mypassword@160.187.229.179:6379";

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> redisConnection;

    @Bean
    public RedisClient redisClient() {
        redisClient = RedisClient.create(REDIS_URL);
        return redisClient;
    }

    @Bean
    public StatefulRedisConnection<String, String> redisConnection(RedisClient redisClient) {
        redisConnection = redisClient.connect();
        handleEventConnection(redisConnection);
        return redisConnection;
    }

    private void handleEventConnection(StatefulRedisConnection<String, String> connection) {
        // Lettuce không có API sự kiện giống ioredis, nhưng ta có thể kiểm tra trạng thái thủ công
        new Thread(() -> {
            while (true) {
                try {
                    if (connection.isOpen()) {
                        logger.info("connection redis - Connection status: connected");
                        RedisCommands<String, String> sync = connection.sync();
                        sync.ping(); // Kiểm tra kết nối
                    }
                    Thread.sleep(15000); // Kiểm tra mỗi 5 giây
                } catch (Exception e) {
                    logger.error("connection redis - Connection status: error {}", e.getMessage());
                    logger.info("connection redis - Connection status: reconnecting");
                    // Lettuce tự động reconnect, không cần xử lý thủ công
                }
            }
        }).start();

        // Lắng nghe sự kiện đóng kết nối
//        connection.addListener(new io.lettuce.core.ConnectionEventListener() {
//            @Override
//            public void onConnectionClosed() {
//                logger.info("connection redis - Connection status: disconnected");
//            }
//        });
    }

    @PreDestroy
    public void shutdown() {
        if (redisConnection != null) {
            redisConnection.close();
        }
        if (redisClient != null) {
            redisClient.shutdown();
            logger.info("Redis client shutdown");
        }
    }

    // Getter để lấy redisConnection ở nơi khác
    public StatefulRedisConnection<String, String> getRedisConnection() {
        return redisConnection;
    }
}