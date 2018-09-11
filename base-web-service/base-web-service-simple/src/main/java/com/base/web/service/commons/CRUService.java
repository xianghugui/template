package com.base.web.service.commons;

import com.base.web.bean.po.GenericPo;
import com.base.web.dao.CRUMapper;
import com.base.web.dao.InsertMapper;
import com.base.web.dao.QueryMapper;
import com.base.web.dao.UpdateMapper;

/**
 * @author
 */
public interface CRUService<Po extends GenericPo<Pk>, Pk> extends
        SimpleInsertService<Po, Pk>,
        SimpleQueryService<Po, Pk>,
        SimpleUpdateService<Po,Pk> {
    CRUMapper<Po, Pk> getCRUMapper();

    @Override
    default InsertMapper<Po> getInsertMapper() {
        return getCRUMapper();
    }

    @Override
    default QueryMapper<Po, Pk> getQueryMapper() {
        return getCRUMapper();
    }

    @Override
    default UpdateMapper<Po> getUpdateMapper() {
        return getCRUMapper();
    }
}
