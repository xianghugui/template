package com.base.web.socket.message;

import com.base.web.socket.WebSocketSessionListener;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * websocket消息管理器，用于使用websocket进行消息推送
 *
 */
public interface WebSocketMessageManager extends WebSocketSessionListener {

    /**
     * 发送一个消息
     *
     * @param message 消息实例
     * @return 是否发送成功
     */
    boolean publish(WebSocketMessage message) throws IOException;

    boolean subscribe(String type, Long userId, WebSocketSession socketSession);

    boolean deSubscribe(String type, Long userId, WebSocketSession socketSession);
}
