package com.base.web.dao;

import com.base.web.bean.Server;
import com.base.web.bean.common.QueryParam;

import java.util.List;

public interface ServerMapper extends GenericMapper<Server, Long> {

    Server queryServerInfo(Long id);
    List<Server> queryServer(QueryParam param);
    int queryServerTotal(QueryParam param);
}
