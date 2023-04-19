package com.zoctan.api.core.config;

import com.zoctan.api.core.cache.MyRedisCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import javax.annotation.Resource;

/**
 * Redis 캐시 구성
 *
 * @author Zoctan
 * @date 2018/07/11
 */
@Slf4j
@Configuration
@EnableCaching(proxyTargetClass = true)
@ConditionalOnProperty(name = "spring.redis.host")
@EnableConfigurationProperties(RedisProperties.class)
public class RedisCacheConfig extends CachingConfigurerSupport {
  @Resource private RedisConnectionFactory redisConnectionFactory;

  @Bean
  @Override
  public CacheManager cacheManager() {
    // RedisCacheWriter 초기화하기
    final RedisCacheWriter redisCacheWriter =
        RedisCacheWriter.nonLockingRedisCacheWriter(this.redisConnectionFactory);
    final RedisCacheConfiguration defaultCacheConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            // 널 값 캐싱 없음
            // .disableCachingNullValues()
            // 어노테이션을 사용할 때 직렬화, 역직렬화
            .serializeKeysWith(MyRedisCacheManager.STRING_PAIR)
            .serializeValuesWith(MyRedisCacheManager.FASTJSON_PAIR);
    // RedisCacheManager 초기화하기
    return new MyRedisCacheManager(redisCacheWriter, defaultCacheConfig);
  }

  /**
   * @Cacheable, @CachePut, @CacheEvict 등의 어노테이션에 키가 구성되지 않은 경우 이 사용자 지정 키 생성기가 사용됩니다.
   * <p>캐시 키를 사용자 지정할 때 키의 고유성을 보장하기 어려움
   *
   * <p>이 경우 메서드 이름을 지정하는 것이 좋습니다(예: @Cacheable(value="", key="{#root.methodName, #id}").
   */
  @Bean
  @Override
  public KeyGenerator keyGenerator() {
    // 예를 들어, User 클래스 list(Integer 페이지, Integer 크기) 메서드는 다음과 같습니다.
    // 사용자 A 요청: list(1, 2)
    // redis 캐시 키: User.list#1,2
    return (target, method, params) -> {
      final String dot = ".";
      final StringBuilder sb = new StringBuilder(32);
      // 클래스 이름
      sb.append(target.getClass().getSimpleName());
      sb.append(dot);
      // 메서드 이름
      sb.append(method.getName());
      // 매개 변수가 있는 경우
      if (0 < params.length) {
        sb.append("#");
        // 매개 변수 사용
        String comma = "";
        for (final Object param : params) {
          sb.append(comma);
          if (param == null) {
            sb.append("NULL");
          } else {
            sb.append(param.toString());
          }
          comma = ",";
        }
      }
      return sb.toString();
    };
  }

  /** 오류 처리, 주로 로그 인쇄 */
  @Bean
  @Override
  public CacheErrorHandler errorHandler() {
    return new SimpleCacheErrorHandler() {
      @Override
      public void handleCacheGetError(
          final RuntimeException e, final Cache cache, final Object key) {
        log.error("==> cache: {}", cache);
        log.error("==>   key: {}", key);
        log.error("==> error: {}", e.getMessage());
        super.handleCacheGetError(e, cache, key);
      }

      @Override
      public void handleCachePutError(
          final RuntimeException e, final Cache cache, final Object key, final Object value) {
        log.error("==> cache: {}", cache);
        log.error("==>   key: {}", key);
        log.error("==> value: {}", value);
        log.error("==> error: {}", e.getMessage());
        super.handleCachePutError(e, cache, key, value);
      }

      @Override
      public void handleCacheEvictError(
          final RuntimeException e, final Cache cache, final Object key) {
        log.error("==> cache: {}", cache);
        log.error("==>   key: {}", key);
        log.error("==> error: {}", e.getMessage());
        super.handleCacheEvictError(e, cache, key);
      }

      @Override
      public void handleCacheClearError(final RuntimeException e, final Cache cache) {
        log.error("==> cache: {}", cache);
        log.error("==> error: {}", e.getMessage());
        super.handleCacheClearError(e, cache);
      }
    };
  }
}
