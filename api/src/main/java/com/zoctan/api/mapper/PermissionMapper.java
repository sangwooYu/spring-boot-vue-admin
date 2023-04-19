package com.zoctan.api.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.zoctan.api.core.mapper.MyMapper;
import com.zoctan.api.entity.Permission;
import com.zoctan.api.entity.Resource;

import java.util.List;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
public interface PermissionMapper extends MyMapper<Permission> {
  /**
   * 관리 중인 모든 리소스 찾기
   *
   * @return 리소스 목록
   */
  List<Resource> listResourceWithHandle();

  /**
   * 관리 중인 모든 리소스 찾기
   *
   * @param roleId 캐릭터 ID
   * @return 리소스 목록
   */
  List<Resource> listRoleWithResourceByRoleId(@Param("roleId") Long roleId);

  /**
   * 모든 권한 코드 가져오기
   *
   * @return 코드 목록
   */
  @Select("SELECT p.code FROM `permission` p")
  List<String> listAllCode();
}
