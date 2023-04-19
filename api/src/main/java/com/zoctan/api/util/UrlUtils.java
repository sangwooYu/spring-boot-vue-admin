package com.zoctan.api.util;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * URL 도구
 *
 * @author Zoctan
 * @date 2018/07/13
 */
public class UrlUtils {
  private UrlUtils() {}

  /**
   * 요청 /계정/목록의 상대 경로
   *
   * @param request request
   * @return 반환 상대 경로
   */
  public static String getMappingUrl(final ServletRequest request) {
    return getMappingUrl((HttpServletRequest) request);
  }

  public static String getMappingUrl(final HttpServletRequest request) {
    return request.getRequestURI().substring(request.getContextPath().length());
  }
}
