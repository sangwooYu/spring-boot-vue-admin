package com.zoctan.api.mapper;

import org.apache.ibatis.annotations.Param;
import com.zoctan.api.core.mapper.MyMapper;
import com.zoctan.api.entity.RolePermission;

import java.util.List;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
public interface RolePermissionMapper extends MyMapper<RolePermission> {
  /**
   * 역할과 해당 권한 ID를 저장합니다.
   *
   * @param roleId 캐릭터 ID
   * @param permissionIdList 권한 ID 목록
   */
  void saveRolePermission(
      @Param("roleId") Long roleId, @Param("permissionIdList") List<Integer> permissionIdList);
}
