package com.zoctan.api.core.response;

/**
 * 응답 상태 코드 열거 클래스
 *
 * <p>사용자 지정 비즈니스 예외 2*** 시작
 *
 * <p>원래 클래스 예외 4*** 시작
 *
 * @author Zoctan
 * @date 2018/07/14
 */
public enum ResultCode {
  SUCCEED_REQUEST_FAILED_RESULT(1000, "요청은 성공했지만 결과는 원하는 성공적인 결과가 아님"),

  FIND_FAILED(2000, "쿼리 실패"),

  SAVE_FAILED(2001, "저장 실패"),

  UPDATE_FAILED(2002, "업데이트 실패"),

  DELETE_FAILED(2003, "삭제하지 못함"),

  DUPLICATE_NAME(2004, "중복 계정 이름"),

  DATABASE_EXCEPTION(4001, "데이터베이스 예외"),

  UNAUTHORIZED_EXCEPTION(4002, "인증 예외"),

  VIOLATION_EXCEPTION(4003, "예외 유효성 검사");

  private final int value;

  private final String reason;

  ResultCode(final int value, final String reason) {
    this.value = value;
    this.reason = reason;
  }

  public int getValue() {
    return this.value;
  }

  public String getReason() {
    return this.reason;
  }

  public String format(final Object... objects) {
    return objects.length > 0 ? String.format(this.getReason(), objects) : this.getReason();
  }
}
