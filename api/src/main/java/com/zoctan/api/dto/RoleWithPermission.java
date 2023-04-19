package com.zoctan.api.dto;

import com.zoctan.api.entity.Role;

import java.util.List;

/**
 * @author Zoctan
 * @date 2018/10/16
 */
public class RoleWithPermission extends Role {
  /** 역할에 해당하는 권한 ID 목록 */
  private List<Integer> permissionIdList;

  public List<Integer> getPermissionIdList() {
    return this.permissionIdList;
  }

  public void setPermissionIdList(final List<Integer> permissionIdList) {
    this.permissionIdList = permissionIdList;
  }
}
