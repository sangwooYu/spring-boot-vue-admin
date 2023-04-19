package com.zoctan.api.core.response;

import org.springframework.http.HttpStatus;

/**
 * 응답 생성 도구
 *
 * @author Zoctan
 * @date 2018/06/09
 */
public class ResultGenerator {
  /**
   * 성공적인 응답 결과
   *
   * @param data 콘텐츠
   * @return 응답 결과
   */
  public static <T> Result<T> genOkResult(final T data) {
    return new Result<T>().setCode(HttpStatus.OK.value()).setData(data);
  }

  /**
   * 성공적인 응답 결과
   *
   * @return 응답 결과
   */
  public static <T> Result<T> genOkResult() {
    return genOkResult(null);
  }

  /**
   * 응답하지 않은 결과
   *
   * @param code 상태 코드
   * @param message 뉴스
   * @return 응답 결과
   */
  public static <T> Result<T> genFailedResult(final int code, final String message) {
    return new Result<T>().setCode(code).setMessage(message);
  }

  /**
   * 응답하지 않은 결과
   *
   * @param resultCode 상태 코드 열거
   * @param message 뉴스
   * @return 응답 결과
   */
  public static <T> Result<T> genFailedResult(final ResultCode resultCode, final String message) {
    return genFailedResult(resultCode.getValue(), message);
  }

  /**
   * 응답하지 않은 결과
   *
   * @param resultCode 상태 코드 열거
   * @return 응답 결과
   */
  public static <T> Result<T> genFailedResult(final ResultCode resultCode) {
    return genFailedResult(resultCode.getValue(), resultCode.getReason());
  }

  /**
   * 응답하지 않은 결과
   *
   * @param message 뉴스
   * @return 응답 결과
   */
  public static <T> Result<T> genFailedResult(final String message) {
    return genFailedResult(ResultCode.SUCCEED_REQUEST_FAILED_RESULT.getValue(), message);
  }

  /**
   * 응답하지 않은 결과
   *
   * @return 응답 결과
   */
  public static <T> Result<T> genFailedResult() {
    return genFailedResult(ResultCode.SUCCEED_REQUEST_FAILED_RESULT);
  }
}
