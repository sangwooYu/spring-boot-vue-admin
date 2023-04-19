package com.zoctan.api.core.mapper;

import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.special.InsertListMapper;

/**
 * 다른 인터페이스를 추가하려면 공식 문서를 참고하세요.
 *
 * @author Zoctan
 * @date 2018/05/27
 */
public interface MyMapper<T>
    extends BaseMapper<T>, ConditionMapper<T>, IdsMapper<T>, InsertListMapper<T> {}
