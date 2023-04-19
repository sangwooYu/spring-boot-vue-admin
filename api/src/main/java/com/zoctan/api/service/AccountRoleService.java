package com.zoctan.api.service;

import com.zoctan.api.core.service.Service;
import com.zoctan.api.entity.AccountRole;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
public interface AccountRoleService extends Service<AccountRole> {
  /**
   * 사용자 역할 업데이트
   *
   * @param accountRole 사용자 역할
   */
  void updateRoleIdByAccountId(AccountRole accountRole);
}
