package com.base.web.dao.user;

import com.base.web.dao.GenericMapper;
import com.base.web.bean.po.user.User;

import java.util.List;
import java.util.Map;

/**
 * 后台管理用户数据映射接口
 * Created by generator
 */
public interface UserMapper extends GenericMapper<User, Long> {
    User selectByUserName(String userName);

    void updatePassword(User user);

    int deleteUser(Long id);

    List<Map> addBrandUser(String userId);

    /**
     * 所有未关联品牌的品牌商账户
     * @return
     */
    List<Map> allNoBelongBrandUser();
}
