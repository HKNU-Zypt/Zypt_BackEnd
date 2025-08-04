package zypt.zyptapiserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import zypt.zyptapiserver.repository.RedisCacheRepository;
import zypt.zyptapiserver.repository.RedisRepository;

import java.time.Duration;

@EnableCaching
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.token_server.host}")
    private String tokenHost;
    @Value("${spring.data.redis.token_server.port}")
    private int tokenPort;
    @Value("${spring.data.redis.cache_server.host}")
    private String cacheHost;
    @Value("${spring.data.redis.cache_server.port}")
    private int cachePort;

    // 토큰 서버용 RedisConnectionFactory
    @Bean(name = "tokenConnectionFactory")
    public RedisConnectionFactory tokenConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(tokenHost, tokenPort));
    }

    // 캐시 서버용 RedisConnectionFactory
    @Bean(name = "cacheConnectionFactory")
    public RedisConnectionFactory cacheConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(cacheHost, cachePort));
    }

    // 토큰 서버용 StringRedisTemplate
    @Bean(name = "redisTemplate")
    public StringRedisTemplate tokenRedisTemplate() {
        return new StringRedisTemplate(cacheConnectionFactory());
    }

    // 캐시 서버용 StringRedisTemplate (CacheManager와는 별개로 필요할 때 사용)
    @Bean(name = "cacheRedisTemplate")
    public StringRedisTemplate cacheRedisTemplate() {
        return new StringRedisTemplate(cacheConnectionFactory());
    }

    // Redis 캐시 설정
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .disableCachingNullValues();
    }

    /**
     * Redis 캐시 설정
     * 캐시 서버를 사용하도록 명시
     */
    @Bean
    public RedisCacheManager cacheManager() {
        return RedisCacheManager.builder(cacheConnectionFactory())
                .cacheDefaults(cacheConfiguration())
                .build();
    }


    @Bean
    public RedisRepository redisRepository() {
        return new RedisRepository(tokenRedisTemplate());
    }

    @Bean
    public RedisCacheRepository redisCacheRepository() {
        return new RedisCacheRepository(cacheRedisTemplate());
    }

}

