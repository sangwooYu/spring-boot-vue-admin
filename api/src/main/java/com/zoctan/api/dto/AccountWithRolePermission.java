package com.zoctan.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Transient;
import java.util.List;

/**
 * @author Zoctan
 * @date 2018/10/16
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AccountWithRolePermission extends AccountWithRole {
  /** 사용자의 역할은 권한 코드에 해당합니다. */
  @Transient private List<String> permissionCodeList;
}
