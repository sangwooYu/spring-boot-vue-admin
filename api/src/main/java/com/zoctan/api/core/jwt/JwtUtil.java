package com.zoctan.api.core.jwt;

import com.zoctan.api.core.rsa.RsaUtils;
import com.zoctan.api.util.RedisUtils;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Json 웹 토큰 도구 유효성 검사, 토큰 생성
 *
 * @author Zoctan
 * @date 2018/05/27
 */
@Slf4j
@Component
public class JwtUtil {
  @Resource private JwtConfigurationProperties jwtProperties;
  @Resource private RedisUtils redisUtils;
  @Resource private RsaUtils rsaUtils;

  private Claims getClaims(final String token) {
    final Jws<Claims> jws = this.parseToken(token);
    return jws == null ? null : jws.getBody();
  }

  /** 토큰에서 계정 이름 가져오기 */
  public String getName(final String token) {
    final Claims claims = this.getClaims(token);
    return claims == null ? null : claims.getSubject();
  }

  /**
   * 토큰 발행
   *
   * @param name 계정 이름
   * @param grantedAuthorities 계정 권한 정보 [ADMIN, TEST, ...]
   * @param isAdmin 관리 백오피스인가요?
   */
  public String sign(
      final String name,
      final Collection<? extends GrantedAuthority> grantedAuthorities,
      final boolean isAdmin) {
    // 중복 작성을 방지하는 기능적 토큰 생성
    final Supplier<String> createToken = () -> this.createToken(name, grantedAuthorities, isAdmin);
    // 캐시에 계정 토큰이 있는지 확인하기
    final String token = (String) this.redisUtils.getValue(name);
    // 로그인하지 않음
    if (StringUtils.isBlank(token)) {
      return createToken.get();
    }
    final boolean isValidate = (boolean) this.redisUtils.getValue(token);
    // 토큰이 여전히 유효합니다.
    if (isValidate) {
      // 삭제, 재발급
      this.redisUtils.delete(name);
      this.redisUtils.delete(token);
      return createToken.get();
    }
    return createToken.get();
  }

  /**
   * Redis에서 계정의 토큰 캐시 지우기
   *
   * @param name 계정 이름
   */
  public void invalidRedisToken(final String name) {
    // 미래에 token 유효하지 않음으로 설정
    final String token = (String) this.redisUtils.getValue(name);
    Optional.ofNullable(token).ifPresent(_token -> this.redisUtils.setValue(_token, false));
  }

  /** 요청 헤더 또는 요청 매개변수에서 token */
  public String getTokenFromRequest(final HttpServletRequest httpRequest) {
    final String header = this.jwtProperties.getHeader();
    final String token = httpRequest.getHeader(header);
    return StringUtils.isNotBlank(token) ? token : httpRequest.getParameter(header);
  }

  /** 계정 인증으로 돌아가기 */
  public UsernamePasswordAuthenticationToken getAuthentication(
      final String name, final String token) {
    // 토큰의 페이로드 구문 분석하기
    final Claims claims = this.getClaims(token);
    // JwtAnthenticationFilter 인터셉터가 이미 토큰이 유효한지 확인했으므로, get null 포인터 힌트는 무시할 수 있습니다.
    assert claims != null;
    final String claimKeyAuth = this.jwtProperties.getClaimKeyAuth();
    // 계정 역할 목록
    final List<String> authList = Arrays.asList(claims.get(claimKeyAuth).toString().split(","));
    // 요소를 GrantedAuthority 인터페이스 컬렉션으로 변환하기
    final Collection<? extends GrantedAuthority> authorities =
        authList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    final User user = new User(name, "", authorities);
    return new UsernamePasswordAuthenticationToken(user, null, authorities);
  }

  /** 토큰이 올바른지 확인 */
  public boolean validateToken(final String token) {
    boolean isValidate = true;
    final Object redisTokenValidate = this.redisUtils.getValue(token);
    // redis 배포에 문제가 있을 수 있습니다.
    // 또는 캐시가 지워져 토큰 키가 존재하지 않습니다.
    if (redisTokenValidate != null) {
      isValidate = (boolean) redisTokenValidate;
    }
    // 토큰이 올바르게 구문 분석되었고 redis에 캐시된 토큰이 유효합니다.
    return this.parseToken(token) != null && isValidate;
  }

  /** 생성 token */
  private String createToken(
      final String name,
      final Collection<? extends GrantedAuthority> grantedAuthorities,
      final boolean isAdmin) {
    // 계정의 역할 문자열을 가져옵니다(예: USER,ADMIN).
    final String authorities =
        grantedAuthorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
    JwtUtil.log.debug("==> Account<{}> authorities: {}", name, authorities);

    // 만료 시간
    final Duration expireTime;
    if (isAdmin) {
      expireTime = this.jwtProperties.getAdminExpireTime();
    } else {
      expireTime = this.jwtProperties.getWechatExpireTime();
    }
    // 현재 시간 + 유효 시간
    final Date expireDate = new Date(System.currentTimeMillis() + expireTime.toMillis());
    // 토큰 생성(예: "무기명 abc1234")
    final String token =
        this.jwtProperties.getTokenType()
            + " "
            + Jwts.builder()
                // 계정 이름 설정
                .setSubject(name)
                // 권한 속성 추가하기
                .claim(this.jwtProperties.getClaimKeyAuth(), authorities)
                // 만료 시간 설정
                .setExpiration(expireDate)
                // 서명 생성을 위한 개인 키 암호화
                .signWith(SignatureAlgorithm.RS256, this.rsaUtils.loadPrivateKey())
                // 허프만 코딩과 결합된 LZ77 알고리즘을 사용한 압축
                .compressWith(CompressionCodecs.DEFLATE)
                .compact();
    // 계정 토큰 저장
    // JWT 자체는 계정이 만료되지 않은 한 로그아웃한 후에도 유효하므로 무효화를 확인할 수 있는 유일한 방법은 redis 캐시를 이용하는 것입니다.
    // redis의 토큰은 유효하지 않은 한 확인할 수 있습니다(JWT 자체는 만료 여부를 확인할 수 있는 반면 redis는 만료되면 삭제됨).
    // 참 유효
    this.redisUtils.setValue(token, true, expireTime);
    // Redis 만료 시간 및 JWT 일관성
    this.redisUtils.setValue(name, token, expireTime);
    JwtUtil.log.debug("==> Redis set Account<{}> token: {}", name, token);
    return token;
  }

  /** 분석 token */
  private Jws<Claims> parseToken(final String token) {
    try {
      return Jwts.parser()
          // 공개 키 복호화
          .setSigningKey(this.rsaUtils.loadPublicKey())
          .parseClaimsJws(token.replace(this.jwtProperties.getTokenType(), ""));
    } catch (final SignatureException e) {
      // 서명 예외
      JwtUtil.log.debug("Invalid JWT signature");
    } catch (final MalformedJwtException e) {
      // 서식 오류
      JwtUtil.log.debug("Invalid JWT token");
    } catch (final ExpiredJwtException e) {
      // 만료됨
      JwtUtil.log.debug("Expired JWT token");
    } catch (final UnsupportedJwtException e) {
      // 이 JWT는 지원되지 않습니다.
      JwtUtil.log.debug("Unsupported JWT token");
    } catch (final IllegalArgumentException e) {
      // 매개변수 오류 예외
      JwtUtil.log.debug("JWT token compact of handler are invalid");
    }
    return null;
  }
}
