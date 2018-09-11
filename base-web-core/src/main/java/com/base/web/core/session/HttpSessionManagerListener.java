package com.base.web.core.session;

import com.base.web.bean.po.user.User;

import javax.servlet.http.HttpSession;


/**
 * Created by   on 16-6-2.
 */
public interface HttpSessionManagerListener {
    void onUserLogin(User user,HttpSession session);

    void onUserLoginOut(Long userId, HttpSession session);
}
