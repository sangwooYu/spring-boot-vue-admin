package com.zoctan.api.service;

import com.zoctan.api.core.service.Service;
import com.zoctan.api.entity.Permission;
import com.zoctan.api.entity.Resource;

import java.util.List;

/**
 * @author Zoctan
 * @date 2018/05/17
 */
public interface PermissionService extends Service<Permission> {
  /**
   * 관리 중인 모든 리소스 찾기
   *
   * @return 리소스 목록
   */
  List<Resource> listResourceWithHandle();

  /**
   * 역할 권한 제어 리소스 찾기
   *
   * @param roleId 캐릭터 ID
   * @return 리소스 목록
   */
  List<Resource> listRoleWithResourceByRoleId(Long roleId);
}
