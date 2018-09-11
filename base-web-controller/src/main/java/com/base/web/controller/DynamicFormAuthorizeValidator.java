package com.base.web.controller;

import com.base.web.bean.po.user.User;

import java.util.Map;

/**
 * Created by   on 16-5-16.
 */
public interface DynamicFormAuthorizeValidator {
    boolean validate(String formName, User user,Map<String,Object> params, String... actions);
}
