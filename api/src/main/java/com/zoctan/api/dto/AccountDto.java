package com.zoctan.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
@Data
public class AccountDto {
  /** 사용자 아이디 */
  private Long id;

  /** 사서함 */
  @NotEmpty(message = "사서함을 비울 수 없습니다.")
  @Email
  private String email;

  /** 사용자 이름 */
  @NotEmpty(message = "사용자 이름은 비워 둘 수 없습니다.")
  @Size(min = 3, message = "사용자 아이디 길이는 3보다 길어야 합니다.")
  private String name;

  /** 비밀번호 */
  @JSONField(serialize = false)
  @NotEmpty(message = "비밀번호는 비워 둘 수 없습니다.")
  @Size(min = 6, message = "비밀번호 길이는 6보다 길어야 합니다.")
  private String password;

  /** 등록 시간 */
  private Timestamp registerTime;

  /** 마지막 로그인 시간 */
  private Timestamp loginTime;

  private Long roleId;
}
