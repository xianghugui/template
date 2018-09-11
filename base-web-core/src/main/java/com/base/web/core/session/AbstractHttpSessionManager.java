package com.base.web.core.session;

import com.base.web.bean.po.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHttpSessionManager implements HttpSessionManager {

    private List<HttpSessionManagerListener> listeners = new ArrayList<>();

    protected void onUserLogin(User user,HttpSession session) {
        if (listeners != null) {
            listeners.forEach(listener -> listener.onUserLogin(user,session));
        }
    }

    protected void onUserLoginOut(Long userId, HttpSession session) {
        if (listeners != null) {
            listeners.forEach(listener -> listener.onUserLoginOut(userId,session));
        }
    }

    @Autowired(required = false)
    public void setListeners(List<HttpSessionManagerListener> listeners) {
        this.listeners = listeners;
    }

    public List<HttpSessionManagerListener> getListeners() {
        return listeners;
    }

    @Override
    public void addListener(HttpSessionManagerListener listener) {
        listeners.add(listener);
    }

}
