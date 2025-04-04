package com.solve.bookstore.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    @Bean
    public CacheManager cacheManager() {
        try {
            RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10)) // 캐시 만료: 10분
                    .disableCachingNullValues() // null 값 캐싱 안함
                    .serializeKeysWith(
                            RedisSerializationContext.SerializationPair
                                    .fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(
                            RedisSerializationContext.SerializationPair
                                    .fromSerializer(new GenericJackson2JsonRedisSerializer()));

            LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
            connectionFactory.afterPropertiesSet();

            connectionFactory.getConnection().ping();

            return RedisCacheManager.builder(connectionFactory)
                    .cacheDefaults(cacheConfiguration)
                    .build();
        } catch (Exception e) {
            log.warn("Redis 사용이 불가합니다 로컬 캐시를 활성화 합니다.");
            return new ConcurrentMapCacheManager(
                    "sameIsbnBooks",
                    "booksByCategory",
                    "booksByTitle",
                    "booksByAuthor",
                    "booksByTitleAndAuthor"
            );
        }
    }

    @Bean
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Cache get error for key '{}': {}", key, exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.warn("Cache put error for key '{}': {}", key, exception.getMessage());
            }
        };
    }
} 