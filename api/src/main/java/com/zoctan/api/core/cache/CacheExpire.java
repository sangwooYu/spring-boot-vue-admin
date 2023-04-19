package com.zoctan.api.core.cache;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 캐시 만료 노트
 *
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CacheExpire {
  /** 만료 시간, 기본 60초 */
  @AliasFor("expire")
  long value() default 60L;

  /** 만료 시간, 기본 60초 */
  @AliasFor("value")
  long expire() default 60L;
}
