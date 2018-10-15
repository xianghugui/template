package com.base.web.service.impl;

import com.base.web.bean.AssociationBlickListDO;
import com.base.web.dao.AssociationBlickListMapper;
import com.base.web.service.AssociationBlickListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("associationBlickListService")
public class AssociationBlickListServiceImpl extends AbstractServiceImpl<AssociationBlickListDO, Long> implements AssociationBlickListService {

    @Autowired
    private AssociationBlickListMapper associationBlickListMapper;

    @Override
    protected AssociationBlickListMapper getMapper() {
        return this.associationBlickListMapper;
    }
}
