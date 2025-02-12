package com.zoctan.api.service.impl;

import com.zoctan.api.core.exception.ServiceException;
import com.zoctan.api.core.service.AbstractService;
import com.zoctan.api.dto.AccountDto;
import com.zoctan.api.dto.AccountWithRole;
import com.zoctan.api.dto.AccountWithRolePermission;
import com.zoctan.api.entity.Account;
import com.zoctan.api.entity.AccountRole;
import com.zoctan.api.mapper.*;
import com.zoctan.api.service.AccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AccountServiceImpl extends AbstractService<Account> implements AccountService {
  @Resource private AccountMapper accountMapper;
  @Resource private AccountRoleMapper accountRoleMapper;
  @Resource private PermissionMapper permissionMapper;
  @Resource private PasswordEncoder passwordEncoder;
  // 일반 사용자 역할 ID
  private final Long defaultRoleId = 2L;

  @Override
  public List<AccountWithRole> listAllWithRole() {
    return this.accountMapper.listAllWithRole();
  }

  /** 저장하기 전에 비밀번호를 암호화하도록 저장 방법을 다시 작성합니다. */
  @Override
  public void save(final AccountDto accountDto) {
    Account a = this.getBy("name", accountDto.getName());
    if (a != null) {
      throw new ServiceException("사용자 이름이 이미 존재합니다.");
    } else {
      a = this.getBy("email", accountDto.getEmail());
      if (a != null) {
        throw new ServiceException("사서함이 이미 존재합니다.");
      } else {
        // log.info("before password : {}", account.getPassword().trim());
        accountDto.setPassword(this.passwordEncoder.encode(accountDto.getPassword().trim()));
        // log.info("after password : {}", account.getPassword());
        final Account account = new Account();
        BeanUtils.copyProperties(accountDto, account);
        this.accountMapper.insertSelective(account);
        // log.info("Account<{}> id : {}", account.getName(), account.getId());
        this.saveRole(account.getId(), accountDto.getRoleId());
      }
    }
  }

  private void saveRole(final Long accountId, Long roleId) {
    // roleId를 지정하지 않으면 기본 일반 사용자 roleId가 저장됩니다.
    if (roleId == null) {
      roleId = this.defaultRoleId;
    }
    final AccountRole accountRole = new AccountRole();
    accountRole.setAccountId(accountId);
    accountRole.setRoleId(roleId);
    this.accountRoleMapper.insertSelective(accountRole);
  }

  /** 重写update方法 */
  @Override
  public void update(final Account account) {
    // 비밀번호가 변경된 경우
    if (account.getPassword() != null && account.getPassword().length() >= 6) {
      // 비밀번호 변경 후 암호화 필요
      account.setPassword(this.passwordEncoder.encode(account.getPassword().trim()));
    }
    this.accountMapper.updateByPrimaryKeySelective(account);
  }

  @Override
  public List<AccountWithRole> findWithRoleBy(final Map<String, Object> params) {
    return this.accountMapper.findWithRoleBy(params);
  }

  @Override
  public AccountWithRolePermission findDetailBy(final String column, final Object params) {
    final Map<String, Object> map = new HashMap<>(1);
    map.put(column, params);
    return this.accountMapper.findDetailBy(map);
  }

  @Override
  public AccountWithRolePermission findDetailByName(final String name)
      throws UsernameNotFoundException {
    final AccountWithRolePermission account = this.findDetailBy("name", name);
    if (account == null) {
      throw new UsernameNotFoundException("사용자 이름을 찾을 수 없습니다.");
    }
    if ("슈퍼 관리자".equals(account.getRoleName())) {
      // 모든 권한이 있는 슈퍼 관리자
      account.setPermissionCodeList(this.permissionMapper.listAllCode());
    }
    return account;
  }

  @Override
  public boolean verifyPassword(final String rawPassword, final String encodedPassword) {
    return this.passwordEncoder.matches(rawPassword, encodedPassword);
  }

  @Override
  public void updateLoginTimeByName(final String name) {
    this.accountMapper.updateLoginTimeByName(name);
  }
}
