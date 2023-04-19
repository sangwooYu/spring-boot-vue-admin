package com.zoctan.api.core.exception;

/**
 * 리소스를 찾을 수 없음 예외(조치를 취하기 전에 업데이트 및 삭제를 모두 확인해야 함)
 *
 * @author Zoctan
 * @date 2018/07/20
 */
public class ResourcesNotFoundException extends RuntimeException {
  private static final String DEFAULT_MESSAGE = "리소스가 존재하지 않습니다.";

  public ResourcesNotFoundException() {
    super(DEFAULT_MESSAGE);
  }

  public ResourcesNotFoundException(final String message) {
    super(message);
  }

  public ResourcesNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
