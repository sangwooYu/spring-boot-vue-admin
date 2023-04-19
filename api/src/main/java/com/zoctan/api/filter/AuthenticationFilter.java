package com.zoctan.api.filter;

import com.zoctan.api.core.jwt.JwtUtil;
import com.zoctan.api.util.IpUtils;
import com.zoctan.api.util.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 인증 필터
 *
 * @author Zoctan
 * @date 2018/05/27
 */
@Slf4j
@Component
public class AuthenticationFilter implements Filter {
  @Resource private JwtUtil jwtUtil;

  @Override
  public void init(final FilterConfig filterConfig) {
    AuthenticationFilter.log.debug("==> AuthenticationFilter init");
  }

  @Override
  public void doFilter(
      final ServletRequest servletRequest,
      final ServletResponse servletResponse,
      final FilterChain filterChain)
      throws IOException, ServletException {
    final HttpServletRequest request = (HttpServletRequest) servletRequest;
    final HttpServletResponse response = (HttpServletResponse) servletResponse;

    AuthenticationFilter.log.debug(
        "==> IP<{}> Request: [{}] {}",
        IpUtils.getIpAddress(),
        request.getMethod(),
        UrlUtils.getMappingUrl(request));

    // 여러 도메인 요청을 허용하도록 설정
    String[] allowDomains = {"http://localhost:9999", "http://localhost:8080"};
    Set<String> allowOrigins = new HashSet<>(Arrays.asList(allowDomains));
    String origin = request.getHeader("Origin");
    if (allowOrigins.contains(origin)) {
      // 교차 도메인을 허용하도록 구성 설정
      response.setHeader("Access-Control-Allow-Origin", origin);
    }
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader(
        "Access-Control-Allow-Headers", "Content-Type, Content-Length, Authorization");
    // 명시적으로 전달이 허용된 메서드, 권장되지 않음 *
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH, OPTIONS");
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader("Access-Control-Expose-Headers", "*");

    // 사전 요청 후 바로 반환
    // 반환 코드는 200이어야 하며, 그렇지 않으면 요청이 실패한 것으로 간주됩니다.
    if (HttpMethod.OPTIONS.matches(request.getMethod())) {
      return;
    }

    final String token = this.jwtUtil.getTokenFromRequest(request);
    if (!StringUtils.isEmpty(token)) {
      final String name = this.jwtUtil.getName(token);
      AuthenticationFilter.log.debug("==> Account<{}> token: {}", name, token);

      if (name != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        if (this.jwtUtil.validateToken(token)) {
          final UsernamePasswordAuthenticationToken authentication =
              this.jwtUtil.getAuthentication(name, token);

          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          // 인증된 계정을 보안 컨텍스트에 삽입하기
          // 그런 다음 컨트롤러 컨트롤러의 항목에서 직접 주체 또는 인증을 받을 수 있습니다.
          SecurityContextHolder.getContext().setAuthentication(authentication);
          AuthenticationFilter.log.debug(
              "==> Account<{}> is authorized, set security context", name);
        }
      }
    } else {
      AuthenticationFilter.log.debug(
          "==> IP<{}> Without token, Request: [{}] {}",
          IpUtils.getIpAddress(),
          request.getMethod(),
          UrlUtils.getMappingUrl(request));
    }
    filterChain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    AuthenticationFilter.log.debug("==> AuthenticationFilter destroy");
  }
}
