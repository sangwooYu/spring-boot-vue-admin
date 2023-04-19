package com.zoctan.api.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 도구
 *
 * @author Zoctan
 * @date 2018/05/27
 */
@Component
public class RedisUtils {
  @Resource private RedisTemplate<String, Object> redisTemplate;

  // =============================common============================

  /**
   * 캐시 만료 시간 설정
   *
   * @param key 키
   * @param timeout 시간
   * @return {Boolean}
   */
  public Boolean setExpire(@NotBlank final String key, @NotBlank final Duration timeout) {
    if (timeout.getSeconds() > 0) {
      return this.redisTemplate.expire(key, timeout.getSeconds(), TimeUnit.SECONDS);
    }
    return false;
  }

  /**
   * 캐시 만료 시간 확인
   *
   * @param key 키
   * @return 시간(초) 영구인 경우 0
   */
  public Long getExpire(@NotBlank final String key) {
    return this.redisTemplate.getExpire(key, TimeUnit.SECONDS);
  }

  /**
   * key 존재합니까?
   *
   * @param key 키
   * @return {Boolean}
   */
  public Boolean hasKey(@NotBlank final String key) {
    return this.redisTemplate.hasKey(key);
  }

  /**
   * 캐시 삭제
   *
   * @param keys 키
   */
  public Boolean delete(@NotBlank final String... keys) {
    return keys.length
        == Optional.ofNullable(this.redisTemplate.delete(Arrays.asList(keys))).orElse(-1L);
  }

  // ============================String=============================

  /**
   * 공통 캐시 가져오기
   *
   * @param key 키
   * @return 가치
   */
  public Object getValue(@NotBlank final String key) {
    return this.redisTemplate.opsForValue().get(key);
  }

  /**
   * 일반 캐시 설정
   *
   * @param key 키
   * @param value 가치
   */
  public void setValue(@NotBlank final String key, @NotBlank final Object value) {
    this.redisTemplate.opsForValue().set(key, value);
  }

  /**
   * 일반 캐시 설정
   *
   * @param key 키
   * @param value 가치
   * @param timeout 0보다 작거나 같은 시간은 무한대로 설정됩니다.
   */
  public void setValue(
      @NotBlank final String key, @NotBlank final Object value, @NotBlank final Duration timeout) {
    this.redisTemplate.opsForValue().set(key, value, timeout);
  }

  /**
   * 증분
   *
   * @param key 키
   * @param delta 몇 개 증가하려면(0보다 커야함)
   * @return 지정된 값을 추가한 후의 키 값입니다.
   */
  public Long incrementValue(@NotBlank final String key, @NotBlank final long delta) {
    if (delta > 0) {
      throw new RuntimeException("递增因子必须大于0");
    }
    return this.redisTemplate.opsForValue().increment(key, delta);
  }

  /**
   * 감소
   *
   * @param key 키
   * @param delta 몇 개 줄이려면(0 미만)
   * @return 지정된 값 이후의 키 값을 감소시킵니다.
   */
  public Long decrementValue(@NotBlank final String key, @NotBlank final long delta) {
    if (delta < 0) {
      throw new RuntimeException("감소 계수는 0보다 커야 합니다.");
    }
    return this.redisTemplate.opsForValue().increment(key, -delta);
  }

  // ================================Map=================================

  /**
   * HashGet
   *
   * @param key 키
   * @param item 항목
   * @return 가치
   */
  public Object getHash(@NotBlank final String key, @NotBlank final String item) {
    return this.redisTemplate.opsForHash().get(key, item);
  }

  /**
   * 해시키에 해당하는 모든 키를 가져옵니다.
   *
   * @param key 키
   * @return 해당 여러 키 값
   */
  public Map<Object, Object> getHash(@NotBlank final String key) {
    return this.redisTemplate.opsForHash().entries(key);
  }

  /**
   * HashSet
   *
   * @param key 키
   * @param map 여러 키 값에 대응
   */
  public void putHash(@NotBlank final String key, @NotBlank final Map<String, Object> map) {
    this.redisTemplate.opsForHash().putAll(key, map);
  }

  /**
   * 해시설정 및 시간 설정
   *
   * @param key 키
   * @param map 여러 키 값에 대응
   * @param timeout 시간
   */
  public void putHash(
      @NotBlank final String key,
      @NotBlank final Map<String, Object> map,
      @NotBlank final Duration timeout) {
    this.redisTemplate.opsForHash().putAll(key, map);
    this.setExpire(key, timeout);
  }

  /**
   * 해시 테이블에 데이터를 입력하면 존재하지 않는 경우 생성됩니다.
   *
   * @param key 키
   * @param item 항목
   * @param value 가치
   */
  public void putHash(
      @NotBlank final String key, @NotBlank final String item, @NotBlank final Object value) {
    this.redisTemplate.opsForHash().put(key, item, value);
  }

  /**
   * 해시 테이블에 데이터를 입력하면 존재하지 않는 경우 생성됩니다.
   *
   * @param key 키
   * @param item 항목
   * @param value 가치
   * @param timeout 시간 참고: 기존 해시 테이블에 시간이 있는 경우 이 시간이 원래 시간을 대체합니다.
   */
  public void putHash(
      @NotBlank final String key,
      @NotBlank final String item,
      @NotBlank final Object value,
      @NotBlank final Duration timeout) {
    this.redisTemplate.opsForHash().put(key, item, value);
    this.setExpire(key, timeout);
  }

  /**
   * 해시 테이블에서 값 삭제하기
   *
   * @param key 키
   * @param item 항목
   */
  public void deleteHash(@NotBlank final String key, @NotBlank final Object... item) {
    this.redisTemplate.opsForHash().delete(key, item);
  }

  /**
   * 해시 테이블에 항목에 대한 값이 있는지 확인합니다.
   *
   * @param key 키
   * @param item 항목
   * @return {Boolean}
   */
  public Boolean hasKeyHash(@NotBlank final String key, @NotBlank final String item) {
    return this.redisTemplate.opsForHash().hasKey(key, item);
  }

  /**
   * 해시 증분값이 존재하지 않으면 새로 생성하고 새 값을 반환합니다.
   *
   * @param key 키
   * @param item 항목
   * @param by 몇 개 증가하려면(0보다 큼)
   * @return 지정된 값을 추가한 후의 키 값입니다.
   */
  public Double incrementHash(
      @NotBlank final String key, @NotBlank final String item, @NotBlank final double by) {
    return this.redisTemplate.opsForHash().increment(key, item, by);
  }

  /**
   * hash递减
   *
   * @param key 키
   * @param item 항목
   * @param by 표시를 줄이려면(0 미만)
   * @return 지정된 값 이후의 키 값을 감소시킵니다.
   */
  public Double decrementHash(
      @NotBlank final String key, @NotBlank final String item, @NotBlank final double by) {
    return this.redisTemplate.opsForHash().increment(key, item, -by);
  }

  // ============================set=============================

  /**
   * 키를 기준으로 집합의 모든 값 가져오기
   *
   * @param key 키
   * @return Set<Object>
   */
  public Set<Object> getSet(@NotBlank final String key) {
    return this.redisTemplate.opsForSet().members(key);
  }

  /**
   * 존재 여부에 관계없이 값을 기준으로 집합에서 검색합니다.
   *
   * @param key 키
   * @param value 가치
   * @return {Boolean}
   */
  public Boolean hasKeySet(@NotBlank final String key, @NotBlank final Object value) {
    return this.redisTemplate.opsForSet().isMember(key, value);
  }

  /**
   * 세트 캐시에 데이터 넣기
   *
   * @param key 키
   * @param values 가치
   * @return 배치된 항목 수
   */
  public Long addSet(@NotBlank final String key, @NotBlank final Object... values) {
    return this.redisTemplate.opsForSet().add(key, values);
  }

  /**
   * 설정된 데이터를 캐시에 저장
   *
   * @param key 키
   * @param timeout 시간
   * @param values 가치
   * @return 배치된 항목 수
   */
  public Long addSet(
      @NotBlank final String key,
      @NotBlank final Duration timeout,
      @NotBlank final Object... values) {
    final Long num = this.redisTemplate.opsForSet().add(key, values);
    this.setExpire(key, timeout);
    return num;
  }

  /**
   * 설정된 캐시의 길이를 가져옵니다.
   *
   * @param key 키
   * @return 캐시 길이
   */
  public Long getSetSize(@NotBlank final String key) {
    return this.redisTemplate.opsForSet().size(key);
  }

  /**
   * 값의 값을 제거합니다.
   *
   * @param key 키
   * @param values 가치
   * @return 제거 횟수
   */
  public Long removeSet(@NotBlank final String key, @NotBlank final Object... values) {
    return this.redisTemplate.opsForSet().remove(key, values);
  }
  // ===============================list=================================

  /**
   * 목록 캐시 내용 가져오기
   *
   * @param key 키
   * @param start 시작
   * @param end 모든 값에 대해 0에서 -1까지 끝납니다.
   * @return 목록 캐시 내용
   */
  public List<Object> getList(
      @NotBlank final String key, @NotBlank final Long start, @NotBlank final Long end) {
    return this.redisTemplate.opsForList().range(key, start, end);
  }

  /**
   * 목록 캐시 길이 가져오기
   *
   * @param key 키
   * @return 목록 캐시 길이
   */
  public Long getListSize(@NotBlank final String key) {
    return this.redisTemplate.opsForList().size(key);
  }

  /**
   * 인덱스별로 목록의 값 가져오기
   *
   * @param key 키
   * @param index 인덱스 인덱스>=0, 0 테이블의 머리, 1 두 번째 요소 등; 인덱스<0, -1, 테이블의 끝, -2 두 번째 요소 등
   * @return 인덱스별로 목록의 값
   */
  public Object getListIndex(@NotBlank final String key, @NotBlank final Long index) {
    return this.redisTemplate.opsForList().index(key, index);
  }

  /**
   * 캐시에 목록 넣기
   *
   * @param key 키
   * @param value 가치
   * @return 배치된 항목 수
   */
  public Long pushList(@NotBlank final String key, @NotBlank final Object value) {
    return this.redisTemplate.opsForList().rightPush(key, value);
  }

  /**
   * 캐시에 목록 넣기
   *
   * @param key 키
   * @param value 가치
   * @param timeout 시간
   */
  public Long pushList(
      @NotBlank final String key, @NotBlank final Object value, @NotBlank final Duration timeout) {
    final Long num = this.redisTemplate.opsForList().rightPush(key, value);
    this.setExpire(key, timeout);
    return num;
  }

  /**
   * 캐시에 목록 넣기
   *
   * @param key 키
   * @param value 가치
   * @return 배치된 항목 수
   */
  public Long pushList(@NotBlank final String key, @NotBlank final List<Object> value) {
    return this.redisTemplate.opsForList().rightPushAll(key, value);
  }

  /**
   * 캐시에 목록 넣기
   *
   * @param key 키
   * @param value 가치
   * @param timeout 시간
   * @return 배치된 항목 수
   */
  public Long pushList(
      @NotBlank final String key,
      @NotBlank final List<Object> value,
      @NotBlank final Duration timeout) {
    final Long num = this.redisTemplate.opsForList().rightPushAll(key, value);
    this.setExpire(key, timeout);
    return num;
  }

  /**
   * 목록의 데이터 항목을 인덱스에 따라 수정합니다.
   *
   * @param key 키
   * @param index 색인
   * @param value 값
   */
  public void updateListIndex(
      @NotBlank final String key, @NotBlank final Long index, @NotBlank final Object value) {
    this.redisTemplate.opsForList().set(key, index, value);
  }

  /**
   * 값에 대한 N 값 제거
   *
   * @param key 키
   * @param count 제거할 수 있는 개수
   * @param value 가치
   * @return 제거 횟수
   */
  public Long removeList(
      @NotBlank final String key, @NotBlank final Long count, @NotBlank final Object value) {
    return this.redisTemplate.opsForList().remove(key, count, value);
  }
}
