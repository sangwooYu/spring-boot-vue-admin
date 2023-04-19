package com.zoctan.api.core.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * 매개변수 확인
 * https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#section-constraint-violation-methods
 *
 * @author Zoctan
 * @date 2018/05/27
 */
@Configuration
public class ValidatorConfig {
  @Bean
  public MethodValidationPostProcessor methodValidationPostProcessor() {
    final MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
    // 빠른 실패 시 반환하도록 유효성 검사기 모드 설정
    postProcessor.setValidator(this.validatorFailFast());
    return postProcessor;
    // 기본값은 모든 실패한 유효성 검사 메시지 모음을 반환하는 일반 모드입니다.
    // return new MethodValidationPostProcessor();
  }

  @Bean
  public Validator validatorFailFast() {
    final ValidatorFactory validatorFactory =
        Validation.byProvider(HibernateValidator.class)
            .configure()
            .addProperty("hibernate.validator.fail_fast", "true")
            .buildValidatorFactory();
    return validatorFactory.getValidator();
  }
}
