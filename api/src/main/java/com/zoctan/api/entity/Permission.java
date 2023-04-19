package com.zoctan.api.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
@Data
public class Permission {
  /** 권한 ID */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 권한에 해당하는 리소스 */
  private String resource;

  /** 권한 코드/와일드카드(코드에서 @hasAuthority(xx)에 해당) */
  private String code;

  /** 해당 리소스 작업 */
  private String handle;
}
