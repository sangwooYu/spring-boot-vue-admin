package com.zoctan.api.core.exception;

import com.zoctan.api.core.response.ResultCode;
import com.zoctan.api.core.response.Result;
import com.zoctan.api.core.response.ResultGenerator;
import com.zoctan.api.util.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * 통일된 예외 처리 비즈니스 예외의 경우: 브라우저 캐싱을 피하기 위해 반환 헤더의 Http 상태 코드에 항상 500을 사용하고 응답 결과에 예외의 상태 코드를 지정합니다.
 *
 * @author Zoctan
 * @date 2018/06/09
 */
@Slf4j
@RestControllerAdvice
public class ExceptionResolver {
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(ConstraintViolationException.class)
  public Result validatorException(final ConstraintViolationException e) {
    final String msg =
        e.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(","));
    // e.toString 사용자가 알 필요가 없는 더 많은 속성 경로
    log.error("==> 엔티티 예외 유효성 검사: {}", e.toString());
    e.printStackTrace();
    return ResultGenerator.genFailedResult(ResultCode.VIOLATION_EXCEPTION, msg);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({ServiceException.class})
  public Result serviceException(final ServiceException e) {
    log.error("==> 서비스 예외: {}", e.getMessage());
    e.printStackTrace();
    return ResultGenerator.genFailedResult(e.getResultCode(), e.getMessage());
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({ResourcesNotFoundException.class})
  public Result resourcesException(final Throwable e) {
    log.error("==> 리소스 예외: {}", e.getMessage());
    e.printStackTrace();
    return ResultGenerator.genFailedResult(ResultCode.FIND_FAILED);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({SQLException.class, DataAccessException.class})
  public Result databaseException(final Throwable e) {
    log.error("==> 데이터베이스 예외: {}", e.getMessage());
    e.printStackTrace();
    return ResultGenerator.genFailedResult(ResultCode.DATABASE_EXCEPTION);
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
  public Result authException(final Throwable e) {
    log.error("==> 인증 예외: {}", e.getMessage());
    e.printStackTrace();
    return ResultGenerator.genFailedResult(ResultCode.UNAUTHORIZED_EXCEPTION);
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler({AccessDeniedException.class, UsernameNotFoundException.class})
  public Result accountException(final Throwable e) {
    log.error("==> 계정 예외: {}", e.getMessage());
    e.printStackTrace();
    return ResultGenerator.genFailedResult(e.getMessage());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoHandlerFoundException.class)
  public Result apiNotFoundException(final Throwable e, final HttpServletRequest request) {
    log.error("==> API가 존재하지 않습니다: {}", e.getMessage());
    e.printStackTrace();
    return ResultGenerator.genFailedResult(
        "API [" + UrlUtils.getMappingUrl(request) + "] not existed");
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public Result globalException(final HttpServletRequest request, final Throwable e) {
    log.error("==> 전역 예외: {}", e.getMessage());
    e.printStackTrace();
    return ResultGenerator.genFailedResult(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        String.format("%s => %s", UrlUtils.getMappingUrl(request), e.getMessage()));
  }
}
