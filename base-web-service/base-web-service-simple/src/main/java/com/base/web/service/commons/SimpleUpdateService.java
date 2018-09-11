package com.base.web.service.commons;

import org.hsweb.ezorm.core.dsl.Update;
import com.base.web.bean.common.UpdateParam;
import com.base.web.bean.po.GenericPo;
import com.base.web.dao.UpdateMapper;
import com.base.web.service.InsertService;
import com.base.web.service.QueryService;
import com.base.web.service.UpdateService;

import java.util.List;

/**
 * @author
 */
public interface SimpleUpdateService<Po extends GenericPo<Pk>, Pk> extends UpdateService<Po> {

    UpdateMapper<Po> getUpdateMapper();

    void tryValidPo(Po po);

    @Override
    default int update(Po data) {
        tryValidPo(data);
        return createUpdate().fromBean(data).where(GenericPo.Property.id).exec();
    }

    @Override
    default int update(List<Po> data) {
        int i = 0;
        for (Po po : data) {
            i += update(po);
        }
        return i;
    }

    @Override
    default int saveOrUpdate(Po po) {
        if (this instanceof QueryService) {
            Po old = ((QueryService<Po, Pk>) this).selectByPk(po.getId());
            if (old != null)
                return update(po);
            else {
                if (this instanceof InsertService)
                    ((InsertService) this).insert(po);
            }
        } else {
            throw new UnsupportedOperationException("不支持此操作");
        }
        return 1;
    }

    /**
     * 创建dsl更新操作对象，默认使用map进行数据填充,调用此方法,需要通过{@link Update#set(String, Object)}进行属性赋值
     *
     * @return {@link Update}
     * @see Update
     * @see UpdateService#createUpdate(UpdateMapper)
     */
    default Update<Po, UpdateParam<Po>> createUpdate() {
        return UpdateService.createUpdate(getUpdateMapper());
    }

    /**
     * 创建dsl更新操作对象，并指定要操作的数据
     *
     * @return {@link Update}
     * @see Update
     * @see UpdateService#createUpdate(UpdateMapper)
     */
    default Update<Po, UpdateParam<Po>> createUpdate(Po data) {
        return Update.build(getUpdateMapper()::update, new UpdateParam<>(data));
    }

}
