package com.zoctan.api.core.jasypt;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 암호화된 값에 대한 사용자 지정 디스커버러 기본값: ENC(abc) 사용자 지정: MyEnc({abc})
 *
 * @author Zoctan
 * @date 2018/07/20
 */
@Component
public class MyEncryptablePropertyDetector implements EncryptablePropertyDetector {
  /** 접두사 */
  private static final String PREFIX = "MyEnc({";
  /** 접미사 */
  private static final String SUFFIX = "})";

  @Override
  public boolean isEncrypted(final String property) {
    if (StringUtils.isBlank(property)) {
      return false;
    }
    final String trimmedProperty = property.trim();

    return trimmedProperty.startsWith(PREFIX) && trimmedProperty.endsWith(SUFFIX);
  }

  @Override
  public String unwrapEncryptedValue(final String property) {
    return property.substring(PREFIX.length(), property.length() - SUFFIX.length());
  }
}
