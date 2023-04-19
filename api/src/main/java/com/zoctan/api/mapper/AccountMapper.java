package com.zoctan.api.mapper;

import com.zoctan.api.core.mapper.MyMapper;
import com.zoctan.api.dto.AccountWithRole;
import com.zoctan.api.dto.AccountWithRolePermission;
import com.zoctan.api.entity.Account;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
public interface AccountMapper extends MyMapper<Account> {
  /**
   * 모든 사용자와 해당 역할 가져오기
   *
   * @return 사용자 목록
   */
  List<AccountWithRole> listAllWithRole();

  /**
   * WeChat 애플릿 ID로 사용자 가져오기
   *
   * @return 사용자
   */
  Account findByWechatOpenId(@Param("openId") String openId);

  /**
   * 조건별 사용자 가져오기
   *
   * @param params 매개변수
   * @return 사용자 목록
   */
  List<AccountWithRole> findWithRoleBy(final Map<String, Object> params);

  /**
   * 조건별 사용자 정보 검색
   *
   * @param params 매개변수
   * @return 사용자
   */
  AccountWithRolePermission findDetailBy(Map<String, Object> params);

  /**
   * 사용자 이름으로 마지막 로그인 시간 업데이트
   *
   * @param name 사용자 이름
   */
  void updateLoginTimeByName(@Param("name") String name);
}
