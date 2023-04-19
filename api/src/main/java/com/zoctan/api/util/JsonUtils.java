package com.zoctan.api.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import java.util.Arrays;

/**
 * Json 도구
 *
 * @author Zoctan
 * @date 2018/07/11
 */
public class JsonUtils {
  private JsonUtils() {}

  /**
   * 특정 필드 보존
   *
   * @param target 타겟 고객
   * @param fields 필드
   * @return 예약 필드 뒤의 개체
   */
  public static <T> T keepFields(final Object target, final Class<T> clz, final String... fields) {
    final SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
    filter.getIncludes().addAll(Arrays.asList(fields));
    return done(target, clz, filter);
  }

  /**
   * 특정 필드 제거
   *
   * @param target 타겟 고객
   * @param fields 필드
   * @return 필드 제거 후 개체
   */
  public static <T> T deleteFields(
      final Object target, final Class<T> clz, final String... fields) {
    final SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
    filter.getExcludes().addAll(Arrays.asList(fields));
    return done(target, clz, filter);
  }

  private static <T> T done(
      final Object target, final Class<T> clz, final SimplePropertyPreFilter filter) {
    final String jsonString = JSON.toJSONString(target, filter);
    return JSON.parseObject(jsonString, clz);
  }
}
