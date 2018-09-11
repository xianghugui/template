package com.base.web.core.logger;

import com.base.web.bean.po.logger.LoggerInfo;

/**
 * Created by   on 16-4-28.
 */
public interface AccessLoggerPersisting {
    void save(LoggerInfo loggerInfo);
}
