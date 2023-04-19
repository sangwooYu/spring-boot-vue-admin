package com.zoctan.api.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zoctan.api.core.jwt.JwtUtil;
import com.zoctan.api.core.response.Result;
import com.zoctan.api.core.response.ResultGenerator;
import com.zoctan.api.dto.AccountDto;
import com.zoctan.api.dto.AccountWithRole;
import com.zoctan.api.entity.Account;
import com.zoctan.api.service.AccountService;
import com.zoctan.api.service.impl.AccountDetailsServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * @author Zoctan
 * @date 2018/06/09
 */
@RestController
@RequestMapping("/account")
@Validated
public class AccountController {
  @Resource private AccountService accountService;
  @Resource private AccountDetailsServiceImpl userDetailsService;
  @Resource private JwtUtil jwtUtil;

  @PostMapping
  public Result register(
      @RequestBody @Valid final AccountDto account, final BindingResult bindingResult) {
    // {"name":"123456", "password":"123456", "email": "123456@qq.com"}
    if (bindingResult.hasErrors()) {
      //noinspection ConstantConditions
      final String msg = bindingResult.getFieldError().getDefaultMessage();
      return ResultGenerator.genFailedResult(msg);
    } else {
      this.accountService.save(account);
      return this.getToken(account.getName());
    }
  }

  @PreAuthorize("hasAuthority('account:delete')")
  @DeleteMapping("/{id}")
  public Result delete(@PathVariable final Long id, final Principal principal) {
    final Account dbAccount = this.accountService.getById(id);
    if (dbAccount == null) {
      return ResultGenerator.genFailedResult("사용자가 존재하지 않습니다.");
    }
    this.accountService.deleteById(id);
    return ResultGenerator.genOkResult();
  }

  @PostMapping("/password")
  public Result validatePassword(@RequestBody final Account account) {
    final Account dbAccount = this.accountService.getById(account.getId());
    final boolean isValidate =
        this.accountService.verifyPassword(account.getPassword(), dbAccount.getPassword());
    return ResultGenerator.genOkResult(isValidate);
  }

  /** 다른 계정의 세부 정보 업데이트 */
  @PreAuthorize("hasAuthority('account:update')")
  @PutMapping("/{id}")
  public Result updateOthers(
      @PathVariable final Long id, @RequestBody final Account account, final Principal principal) {
    final Account dbAccount = this.accountService.getById(id);
    if (dbAccount == null) {
      return ResultGenerator.genFailedResult("사용자가 존재하지 않습니다.");
    }
    this.accountService.update(account);
    return ResultGenerator.genOkResult();
  }

  /** 나만의 프로필 업데이트 */
  @PutMapping("/detail")
  public Result updateMe(@RequestBody final Account account) {
    this.accountService.update(account);
    final Account dbAccount = this.accountService.getById(account.getId());
    return this.getToken(dbAccount.getName());
  }

  /** 다른 계정에 대한 정보 */
  @PreAuthorize("hasAuthority('account:detail')")
  @GetMapping("/{id}")
  public Result detail(@PathVariable final Long id) {
    final Account dbAccount = this.accountService.getById(id);
    return ResultGenerator.genOkResult(dbAccount);
  }

  /** 자신의 정보 */
  @GetMapping("/detail")
  public Result detail(final Principal principal) {
    final Account dbAccount = this.accountService.findDetailByName(principal.getName());
    return ResultGenerator.genOkResult(dbAccount);
  }

  @PreAuthorize("hasAuthority('account:list')")
  @GetMapping
  public Result list(
      @RequestParam(defaultValue = "0") final Integer page,
      @RequestParam(defaultValue = "0") final Integer size) {
    PageHelper.startPage(page, size);
    final List<AccountWithRole> list = this.accountService.listAllWithRole();
    final PageInfo<AccountWithRole> pageInfo = new PageInfo<>(list);
    return ResultGenerator.genOkResult(pageInfo);
  }

  @PreAuthorize("hasAuthority('account:search')")
  @PostMapping("/search")
  public Result search(@RequestBody final Map<String, Object> param) {
    PageHelper.startPage((Integer) param.get("page"), (Integer) param.get("size"));
    final List<AccountWithRole> list = this.accountService.findWithRoleBy(param);
    final PageInfo<AccountWithRole> pageInfo = new PageInfo<>(list);
    return ResultGenerator.genOkResult(pageInfo);
  }

  @PostMapping("/token")
  public Result login(@RequestBody final Account account) {
    // {"name":"admin", "password":"admin123"}
    // {"email":"admin@qq.com", "password":"admin123"}
    if (account.getName() == null && account.getEmail() == null) {
      return ResultGenerator.genFailedResult("사용자 이름 또는 이메일이 비어 있습니다.");
    }
    if (account.getPassword() == null) {
      return ResultGenerator.genFailedResult("비밀번호가 비어 있습니다.");
    }
    // 用户名登录
    Account dbAccount = null;
    if (account.getName() != null) {
      dbAccount = this.accountService.getBy("name", account.getName());
      if (dbAccount == null) {
        return ResultGenerator.genFailedResult("사용자 이름 오류");
      }
    }
    // 邮箱登录
    if (account.getEmail() != null) {
      dbAccount = this.accountService.getBy("email", account.getEmail());
      if (dbAccount == null) {
        return ResultGenerator.genFailedResult("사서함 오류");
      }
      account.setName(dbAccount.getName());
    }
    // 验证密码
    //noinspection ConstantConditions
    if (!this.accountService.verifyPassword(account.getPassword(), dbAccount.getPassword())) {
      return ResultGenerator.genFailedResult("비밀번호 오류");
    }
    // 更新登录时间
    this.accountService.updateLoginTimeByName(account.getName());
    return this.getToken(account.getName());
  }

  @DeleteMapping("/token")
  public Result logout(final Principal principal) {
    this.jwtUtil.invalidRedisToken(principal.getName());
    return ResultGenerator.genOkResult();
  }

  /** 액세스 token */
  private Result getToken(final String name) {
    final UserDetails accountDetails = this.userDetailsService.loadUserByUsername(name);
    final String token = this.jwtUtil.sign(name, accountDetails.getAuthorities(), true);
    return ResultGenerator.genOkResult(token);
  }
}
