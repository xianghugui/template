package com.base.web.service.commons;

import org.hsweb.ezorm.core.dsl.Delete;
import com.base.web.bean.common.DeleteParam;
import com.base.web.bean.po.GenericPo;
import com.base.web.dao.DeleteMapper;
import com.base.web.service.DeleteService;
import com.base.web.service.GenericService;

/**
 * @author
 */
public interface SimpleDeleteService<Pk> extends DeleteService<Pk> {
    DeleteMapper getDeleteMapper();

    default int delete(Pk pk) {
        return createDelete().where(GenericPo.Property.id, pk).exec();
    }

    /**
     * 创建dsl删除操作对象
     *
     * @return {@link Delete}
     * @see Delete
     * @see GenericService#createDelete(DeleteMapper)
     */
    default Delete<DeleteParam> createDelete() {
        return DeleteService.createDelete(getDeleteMapper());
    }

}
