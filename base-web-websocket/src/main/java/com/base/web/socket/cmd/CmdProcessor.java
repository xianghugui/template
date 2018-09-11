package com.base.web.socket.cmd;


import com.base.web.socket.WebSocketSessionListener;


public interface CmdProcessor extends WebSocketSessionListener {
    /**
     * 获取命令名称
     *
     * @return 命令名称
     */
    String getName();

    /**
     * 执行命令
     *
     * @param cmd 要执行的命令
     * @return 执行结果
     * @throws Exception 异常
     */
    void exec(CMD cmd) throws Exception;


    /**
     * 初始化方法，用于自动注册命令等操作
     *
     * @throws Exception
     */
    void init() throws Exception;
}
