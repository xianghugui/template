package com.base.web.dao;

import com.base.web.bean.common.UpdateParam;

/**
 * @author
 */
public interface UpdateMapper<Po> {
    /**
     * 修改记录信息
     *
     * @param data 要修改的对象
     * @return 影响记录数
     */
    int update(UpdateParam<Po> data);
}
