package com.zoctan.api.mapper;

import com.zoctan.api.core.mapper.MyMapper;
import com.zoctan.api.dto.RoleWithResource;
import com.zoctan.api.entity.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
public interface RoleMapper extends MyMapper<Role> {
  /**
   * 모든 역할과 해당 권한 가져오기
   *
   * @return 캐릭터가 제어하는 리소스 목록
   */
  List<RoleWithResource> listRoles();

  /**
   * 캐릭터 ID별 수정 시간 업데이트
   *
   * @param id 캐릭터 ID
   */
  void updateTimeById(@Param("id") Long id);
}
