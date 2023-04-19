package com.zoctan.api.core.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Json 웹 토큰 배치
 *
 * @author Zoctan
 * @date 2018/06/09
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfigurationProperties {
  /** claim authorities key */
  private String claimKeyAuth;
  /** token 접두사 */
  private String tokenType;
  /** 요청 헤더 또는 요청 매개변수의 key */
  private String header;
  /** 백오피스 만료 시간 관리 */
  private Duration adminExpireTime;
  /** 애플릿 전면에 표시된 만료 시간 */
  private Duration wechatExpireTime;
}
