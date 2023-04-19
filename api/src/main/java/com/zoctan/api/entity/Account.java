package com.zoctan.api.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
@Data
public class Account {
  /** 사용자 ID */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** 사서함 */
  private String email;

  /** 사용자 이름 */
  private String name;

  /** 비밀번호 */
  private String password;

  /** 등록 시간 */
  private Timestamp registerTime;

  /** 마지막 로그인 시간 */
  private Timestamp loginTime;
}
