package com.zoctan.api.aspect;

import com.zoctan.api.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * 컨트롤러 로그 컷
 *
 * @author Zoctan
 * @date 2018/07/13
 */
@Aspect
@Slf4j
@Component
public class ControllerLogAspect {
  /** 시작 시간 */
  private LocalDateTime startTime;

  @Pointcut("execution(* com.zoctan.api.controller..*.*(..))")
  public void controllers() {}

  @Before("controllers()")
  public void deBefore(final JoinPoint joinPoint) {
    // 요청을 수신하고 요청 내용을 기록합니다.
    log.debug("===========================================================");
    log.debug("================  Controller Log Start  ===================");
    log.debug("===========================================================");
    this.startTime = LocalDateTime.now();
    final ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes != null) {
      final HttpServletRequest request = attributes.getRequest();
      log.debug("==> Request: [{}]{}", request.getMethod(), request.getRequestURL());
      log.debug("==> From IP: {}", IpUtils.getIpAddress());
    }
    log.debug(
        "==>  Method: {}",
        joinPoint.getSignature().getDeclaringTypeName() + "#" + joinPoint.getSignature().getName());
    log.debug("==>    Args: {}", Arrays.toString(joinPoint.getArgs()));
  }

  /**
   * 사후 결과 반환
   *
   * @param result 결과
   */
  @AfterReturning(pointcut = "controllers()", returning = "result")
  public void doAfterReturning(final Object result) {
    // 요청 처리 시간 차이
    final long difference = ChronoUnit.MILLIS.between(this.startTime, LocalDateTime.now());
    log.debug("==>   Spend: {}s", difference / 1000.0);
    log.debug("==>  Return: {}", result);
    log.debug("================  Controller Log End  =====================");
  }

  /** 예외 발생 후 알림 */
  @AfterThrowing(pointcut = "controllers()", throwing = "e")
  public void doAfterThrowing(final Throwable e) {
    log.debug("==> Exception: {}", e.toString());
    e.printStackTrace();
    log.debug("================  Controller Log End  =====================");
  }
}
