package com.zoctan.api.core.service;

import com.zoctan.api.core.exception.ResourcesNotFoundException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * Service 레이어 기본 인터페이스
 *
 * @author Zoctan
 * @date 2018/05/27
 */
public interface Service<T> {

  /**
   * 물리적 존재감 보장
   *
   * @param id 엔티티 ID
   * @throws ResourcesNotFoundException 신체적 이상 없음
   */
  void assertById(Object id);

  /**
   * 물리적 존재감 보장
   *
   * @param entity 엔티티
   * @throws ResourcesNotFoundException 신체적 이상 없음
   */
  void assertBy(T entity);

  /**
   * 물리적 존재감 보장
   *
   * @param ids ids
   */
  void assertByIds(String ids);

  /**
   * ID를 기준으로 엔티티 수 가져오기
   *
   * @param ids ids
   */
  int countByIds(String ids);

  /**
   * 조건에 따라 엔티티 수 가져오기
   *
   * @param condition 조건
   */
  int countByCondition(Condition condition);

  /**
   * 내구성
   *
   * @param entity 엔티티
   */
  void save(T entity);

  /**
   * 배치 지속성
   *
   * @param entities 엔티티 목록
   */
  void save(List<T> entities);

  /**
   * 마스터 키로 삭제
   *
   * @param id id
   */
  void deleteById(Object id);

  /**
   * 엔티티의 멤버 변수 이름으로 삭제(데이터 테이블의 열 이름이 아님)
   *
   * @param fieldName 필드 이름
   * @param value 필드 값
   * @throws TooManyResultsException 여러 결과 예외
   */
  void deleteBy(String fieldName, Object value) throws TooManyResultsException;

  /**
   * 대량 삭제 ids -> “1,2,3,4”
   *
   * @param ids ids
   */
  void deleteByIds(String ids);

  /**
   * 조건별 삭제
   *
   * @param condition 조건
   */
  void deleteByCondition(Condition condition);

  /**
   * 구성 요소별 업데이트
   *
   * @param entity 엔티티
   */
  void update(T entity);

  /**
   * 조건별 업데이트
   *
   * @param entity 엔티티
   * @param condition 조건
   */
  void updateByCondition(T entity, Condition condition);

  /**
   * 아이디로 찾기
   *
   * @param id id
   * @return 엔티티
   */
  T getById(Object id);

  /**
   * 엔티티의 멤버 변수 이름으로 값을 찾는 데는 다음과 같은 고유 제약 조건이 적용됩니다.
   *
   * @param fieldName 필드 이름
   * @param value 필드 값
   * @return 엔티티
   * @throws TooManyResultsException 여러 결과 예외
   */
  T getBy(String fieldName, Object value) throws TooManyResultsException;

  /**
   * 여러 개의 아이디로 아이디 찾기 -> "1,2,3,4"
   *
   * @param ids ids
   * @return 엔티티 목록
   */
  List<T> listByIds(String ids);

  /**
   * 조건으로 검색
   *
   * @param condition 조건
   * @return 엔티티 목록
   */
  List<T> listByCondition(Condition condition);

  /**
   * 모든 엔티티 가져오기
   *
   * @return 엔티티 목록
   */
  List<T> listAll();
}
