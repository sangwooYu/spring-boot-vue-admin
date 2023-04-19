package com.zoctan.api.core.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.zoctan.api.core.cache.MyRedisCacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * Redis 구성
 *
 * @author Zoctan
 * @date 2018/05/27
 */
@Configuration
public class RedisConfig {
  @Resource private RedisProperties redisProperties;

  @Bean
  @ConfigurationProperties(prefix = "spring.redis.jedis.pool")
  public JedisPoolConfig jedisPoolConfig() {
    return new JedisPoolConfig();
  }

  @Bean
  @ConfigurationProperties(prefix = "spring.redis")
  public RedisConnectionFactory redisConnectionFactory(
      @Qualifier(value = "jedisPoolConfig") final JedisPoolConfig jedisPoolConfig) {
    // 메서드의 @ConfigurationProperties는 적용되지 않습니다.
    // 알 수 없는 버그, 현재로서는 다음과 같이 수동으로 설정합니다.
    // fixme
    // 독립형 jedis
    final RedisStandaloneConfiguration redisStandaloneConfiguration =
        new RedisStandaloneConfiguration(
            this.redisProperties.getHost(), this.redisProperties.getPort());
    redisStandaloneConfiguration.setDatabase(this.redisProperties.getDatabase());
    redisStandaloneConfiguration.setPassword(this.redisProperties.getPassword());

    // 기본 연결 풀 생성자 가져오기
    final JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jpcb =
        (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder)
            JedisClientConfiguration.builder();
    // 기본 연결 풀 생성자를 수정하려면 jedisPoolConifig를 지정하세요.
    jpcb.poolConfig(jedisPoolConfig);
    // 연결 시간 초과
    jpcb.and().connectTimeout(Duration.ofSeconds(10));
    // 생성자를 통해 jedis 클라이언트 구성 구성하기
    final JedisClientConfiguration jedisClientConfiguration = jpcb.build();
    // 독립 실행형 구성 + 클라이언트 구성 = jedis 연결 팩토리
    return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
  }

  /**
   * RedisTemplate을 구성하고 키 및 값에 대한 직렬화 클래스를 구성합니다.
   *
   * <p> 키 직렬화는 StringRedisSerializer를 사용하여 수행되며, 이 경우 키가 왜곡되어 나타납니다.
   */
  @Bean
  public RedisTemplate redisTemplate(
      @Qualifier(value = "redisConnectionFactory") final RedisConnectionFactory factory) {
    final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

    // 키의 직렬화기를 문자열 직렬화기로 설정합니다.
    final StringRedisSerializer stringSerializer = MyRedisCacheManager.STRING_SERIALIZER;

    redisTemplate.setKeySerializer(stringSerializer);
    redisTemplate.setHashKeySerializer(stringSerializer);

    // 값에 대한 직렬화기를 fastjson 직렬화기로 설정합니다.
    final GenericFastJsonRedisSerializer fastSerializer = MyRedisCacheManager.FASTJSON_SERIALIZER;

    redisTemplate.setValueSerializer(fastSerializer);
    redisTemplate.setHashValueSerializer(fastSerializer);

    // 키 시리얼라이저 또는 값 시리얼라이저가 구성되지 않은 경우
    // 그러면 해당 키 직렬화기와 값 직렬화기는 fastjson 직렬화기를 사용합니다.
    redisTemplate.setDefaultSerializer(fastSerializer);

    redisTemplate.setConnectionFactory(factory);
    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }
}
