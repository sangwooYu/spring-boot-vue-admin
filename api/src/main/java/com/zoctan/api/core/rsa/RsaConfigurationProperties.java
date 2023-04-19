package com.zoctan.api.core.rsa;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * RSA 구성
 *
 * @author Zoctan
 * @date 2018/07/20
 */
@Data
@Component
@ConfigurationProperties(prefix = "rsa")
public class RsaConfigurationProperties {
  /** 개인 키 위치 */
  private String privateKeyPath;
  /** 공개 키 위치 */
  private String publicKeyPath;
  /** 파일 또는 문자열만 사용 */
  private boolean useFile;
  /** 개인 키 */
  private String privateKey;
  /** 공개 키 */
  private String publicKey;

  private String publicKeyHead = "-----BEGIN PUBLIC KEY-----";
  private String publicKeyTail = "-----END PUBLIC KEY-----";
  private String privateKeyHead = "-----BEGIN PRIVATE KEY-----";
  private String privateKeyTail = "-----END PRIVATE KEY-----";
}
