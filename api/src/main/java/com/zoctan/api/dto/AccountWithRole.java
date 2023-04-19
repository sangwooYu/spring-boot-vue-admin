package com.zoctan.api.dto;

import com.zoctan.api.entity.Account;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Transient;

/**
 * @author Zoctan
 * @date 2018/10/16
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AccountWithRole extends Account {
  /** 사용자의 역할 ID */
  @Transient private Long roleId;

  /** 사용자의 역할 이름 */
  @Transient private String roleName;
}
