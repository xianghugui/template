package com.base.web.core.authorize;

import com.base.web.bean.po.user.User;

import java.util.Map;

/**
 * 权限验证器
 * Created by   on 16-4-28.
 */
public interface AuthorizeValidator {
    boolean validate(User user, Map<String, Object> param, AuthorizeValidatorConfig config);

    AuthorizeValidatorConfig createConfig();
}
