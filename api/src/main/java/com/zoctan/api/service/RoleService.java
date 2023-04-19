package com.zoctan.api.service;

import com.zoctan.api.core.service.Service;
import com.zoctan.api.dto.RoleWithPermission;
import com.zoctan.api.dto.RoleWithResource;
import com.zoctan.api.entity.Role;

import java.util.List;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
public interface RoleService extends Service<Role> {

  /**
   * 새 캐릭터 만들기
   *
   * @param roleWithPermission 권한 목록이 있는 역할
   */
  void save(RoleWithPermission roleWithPermission);

  /**
   * 역할 업데이트
   *
   * @param roleWithPermission 권한 목록이 있는 역할
   */
  void update(RoleWithPermission roleWithPermission);

  /**
   * 모든 역할과 해당 권한 가져오기
   *
   * @return 캐릭터가 제어하는 리소스 목록
   */
  List<RoleWithResource> listRoleWithPermission();
}
