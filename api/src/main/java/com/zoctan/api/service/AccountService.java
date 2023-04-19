package com.zoctan.api.service;

import com.zoctan.api.core.service.Service;
import com.zoctan.api.dto.AccountDto;
import com.zoctan.api.dto.AccountWithRole;
import com.zoctan.api.dto.AccountWithRolePermission;
import com.zoctan.api.entity.Account;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
public interface AccountService extends Service<Account> {
  /**
   * 사용자 저장
   *
   * @param accountDto 사용자
   */
  void save(AccountDto accountDto);

  /**
   * 모든 사용자와 해당 역할 가져오기
   *
   * @return 사용자 목록
   */
  List<AccountWithRole> listAllWithRole();

  /**
   * 조건별 사용자 검색
   *
   * @param params 매개변수
   * @return 사용자 목록
   */
  List<AccountWithRole> findWithRoleBy(final Map<String, Object> params);

  /**
   * 조건별 사용자 정보 검색
   *
   * @param column 리스팅
   * @param params 매개변수
   * @return 사용자
   */
  AccountWithRolePermission findDetailBy(String column, Object params);

  /**
   * 사용자 이름으로 사용자 정보 검색
   *
   * @param name 사용자 이름
   * @return 사용자
   * @throws UsernameNotFoundException 사용자 이름을 찾을 수 없습니다.
   */
  AccountWithRolePermission findDetailByName(String name) throws UsernameNotFoundException;

  /**
   * 사용자 이름으로 마지막 로그인 시간 업데이트
   *
   * @param name 사용자 이름
   */
  void updateLoginTimeByName(String name);

  /**
   * 사용자 비밀번호 확인
   *
   * @param rawPassword 원본 비밀번호
   * @param encodedPassword 암호화된 비밀번호
   * @return boolean
   */
  boolean verifyPassword(String rawPassword, String encodedPassword);
}
