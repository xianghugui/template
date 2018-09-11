package com.base.web.service.commons;

import com.base.web.bean.common.InsertParam;
import com.base.web.bean.po.GenericPo;
import com.base.web.dao.InsertMapper;
import com.base.web.service.InsertService;

/**
 * @author
 */
public interface SimpleInsertService<Po extends GenericPo<Pk>, Pk> extends InsertService<Po, Pk> {

    InsertMapper<Po> getInsertMapper();

    void tryValidPo(Po data);

    Class<Pk> getPKType();

    @Override
    default Pk insert(Po data) {
        if (getPKType() == Long.class && data.getId() == null) {
            ((GenericPo<Long>) data).setId(System.nanoTime());
        }
        tryValidPo(data);
        getInsertMapper().insert(InsertParam.build(data));
        return data.getId();
    }
}
