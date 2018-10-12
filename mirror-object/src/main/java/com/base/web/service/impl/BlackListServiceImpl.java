package com.base.web.service.impl;

import com.base.web.bean.BlackList;
import com.base.web.dao.BlackListMapper;
import com.base.web.service.BlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("blackListService")
public class BlackListServiceImpl extends AbstractServiceImpl<BlackList, Long> implements BlackListService {

    @Autowired
    private BlackListMapper blackListMapper;

    @Override
    protected BlackListMapper getMapper() {
        return this.blackListMapper;
    }
}
