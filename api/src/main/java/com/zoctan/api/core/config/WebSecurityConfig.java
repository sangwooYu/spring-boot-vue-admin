package com.zoctan.api.core.config;

import com.zoctan.api.filter.AuthenticationFilter;
import com.zoctan.api.filter.MyAuthenticationEntryPoint;
import com.zoctan.api.service.impl.AccountDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * 보안 설정
 *
 * @author Zoctan
 * @date 2018/05/27
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Resource private MyAuthenticationEntryPoint myAuthenticationEntryPoint;
  @Resource private AuthenticationFilter authenticationFilter;

  @Bean
  @Override
  public AccountDetailsServiceImpl userDetailsService() {
    return new AccountDetailsServiceImpl();
  }

  /** 무작위 솔티드 해시 알고리즘을 사용한 비밀번호 암호화 */
  @Bean
  public PasswordEncoder passwordEncoder() {
    // 기본 강도 10, 4~31 사이의 강도를 지정할 수 있습니다.
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
    auth
        // 계정 정보에 대한 맞춤형 액세스
        .userDetailsService(this.userDetailsService())
        // 비밀번호 암호화 설정
        .passwordEncoder(this.passwordEncoder());
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http
        // 페이지 캐싱 비활성화
        .headers()
        .cacheControl()
        .and()
        .and()
        // Cors 인증 닫기
        .cors()
        .disable()
        // CSRF 인증 닫기
        .csrf()
        .disable()
        // 상태 비저장 세션
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        // 예외 처리
        .exceptionHandling()
        // RESTFul에는 로그인 화면이 없으므로 로그인하지 않은 상태만 표시됩니다.
        .authenticationEntryPoint(this.myAuthenticationEntryPoint)
        .and()
        // 신원 필터
        .addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class)
        // 모든 요청에 대한 권한 확인
        .authorizeRequests()
        // 익명 GET 요청 허용
        .antMatchers("/swagger-ui.html**", "/swagger-resources**", "/webjars/**", "/v2/**")
        .permitAll()
        // 익명 POST 요청 허용
        .antMatchers(HttpMethod.POST, "/account", "/account/token")
        .permitAll()
        // 익명 삭제 요청 허용
        .antMatchers(HttpMethod.DELETE, "/upload/image/**", "/upload/video/**")
        .permitAll()
        // 토큰 업데이트
        .antMatchers(HttpMethod.PUT, "/wechat/token")
        .permitAll()
        // 위의 요청을 제외한 모든 요청에는 포렌식 인증이 필요합니다.
        .anyRequest()
        .authenticated();
  }
}
