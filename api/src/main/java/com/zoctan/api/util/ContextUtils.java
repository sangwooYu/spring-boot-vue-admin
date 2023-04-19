package com.zoctan.api.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 상황에 맞는 도구
 *
 * @author Zoctan
 * @date 2018/07/20
 */
public class ContextUtils {
  private ContextUtils() {}

  /**
   * 요청 받기
   *
   * @return request
   */
  public static HttpServletRequest getRequest() {
    final ServletRequestAttributes attributes =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
    return attributes == null ? null : attributes.getRequest();
  }
}
