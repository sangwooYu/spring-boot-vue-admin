package com.zoctan.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
@Data
@Table(name = "account_role")
public class AccountRole {
  /** 사용자 ID */
  @Id
  @Column(name = "account_id")
  private Long accountId;

  /** 캐릭터 ID */
  @Column(name = "role_id")
  private Long roleId;
}
