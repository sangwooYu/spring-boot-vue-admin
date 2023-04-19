package com.zoctan.api.core.constant;

/**
 * 프로젝트 상수
 *
 * @author Zoctan
 * @date 2018/05/27
 */
public final class ProjectConstant {
  /** 개발 환경 */
  public static final String SPRING_PROFILE_DEVELOPMENT = "dev";

  /** 프로덕션 환경 */
  public static final String SPRING_PROFILE_PRODUCTION = "prod";

  /** 테스트 환경 */
  public static final String SPRING_PROFILE_TEST = "test";

  /** 프로젝트 기본 패키지 이름 */
  public static final String BASE_PACKAGE = "com.zoctan.api";

  /** Entity 패키지 */
  public static final String ENTITY_PACKAGE = BASE_PACKAGE + ".entity";

  /** Mapper 패키지 */
  public static final String MAPPER_PACKAGE = BASE_PACKAGE + ".mapper";

  /** Filter 패키지 */
  public static final String FILTER_PACKAGE = BASE_PACKAGE + ".filter";

  /** Service 패키지 */
  public static final String SERVICE_PACKAGE = BASE_PACKAGE + ".service";

  /** ServiceImpl 패키지 */
  public static final String SERVICE_IMPL_PACKAGE = SERVICE_PACKAGE + ".impl";

  /** Controller 패키지 */
  public static final String CONTROLLER_PACKAGE = BASE_PACKAGE + ".controller";

  /** Mapper 플러그인의 기본 인터페이스의 정규화된 이름 */
  public static final String MAPPER_INTERFACE_REFERENCE = BASE_PACKAGE + ".core.mapper.MyMapper";
}
