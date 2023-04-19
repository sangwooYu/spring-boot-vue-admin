package com.zoctan.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
@Data
public class Role {
  /** 캐릭터 ID */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 캐릭터 이름 */
  @NotEmpty(message = "캐릭터 이름은 비워둘 수 없습니다.")
  private String name;

  /** 생성 시간 */
  @Column(name = "create_time")
  private Timestamp createTime;

  /** 시간 수정 */
  @Column(name = "update_time")
  private Timestamp updateTime;
}
