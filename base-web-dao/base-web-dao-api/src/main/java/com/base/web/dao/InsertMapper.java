package com.base.web.dao;

import com.base.web.bean.common.InsertParam;

/**
 * @author
 */
public interface InsertMapper<Po> {
    int insert(InsertParam<Po> param);
}
