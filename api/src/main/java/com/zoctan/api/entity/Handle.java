package com.zoctan.api.entity;

import lombok.Data;

import javax.persistence.Transient;

/**
 * @author Zoctan
 * @date 2018/10/16
 */
@Data
public class Handle {
  /** 해당 권한 ID */
  @Transient private Long id;

  /** 작업 이름 */
  @Transient private String handle;
}
