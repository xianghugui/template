package com.base.web.socket.cmd.support;

import com.base.web.bean.po.user.User;
import com.base.web.core.session.HttpSessionManager;
import com.base.web.socket.cmd.CmdProcessor;
import com.base.web.socket.message.WebSocketMessageManager;
import com.base.web.socket.utils.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;

/**
 * Created by   on 16-5-30.
 */
public abstract class AbstractCmdProcessor implements CmdProcessor {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected HttpSessionManager httpSessionManager;
    protected WebSocketMessageManager webSocketMessageManager;

    @Autowired
    public void setHttpSessionManager(HttpSessionManager httpSessionManager) {
        this.httpSessionManager = httpSessionManager;
    }

    @Autowired
    public void setWebSocketMessageManager(WebSocketMessageManager webSocketMessageManager) {
        this.webSocketMessageManager = webSocketMessageManager;
    }

    public User getUser(WebSocketSession socketSession) {
        return SessionUtils.getUser(socketSession, httpSessionManager);
    }
}
