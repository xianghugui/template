package com.base.web.service.user;

import com.base.web.bean.po.user.User;
import com.base.web.service.GenericService;

import java.util.List;
import java.util.Map;

/**
 * 后台管理用户服务类
 * Created by generator
 */
public interface UserService extends GenericService<User, Long> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户名对应的用户，如果不存在返回null
     * @
     */
    User selectByUserName(String username) ;

    /**
     * 将一个user初始化为超级管理员
     *
     * @param user 要初始化的user对象
     */
    void initAdminUser(User user) ;

    /**
     * 将一个user初始化为游客
     *
     * @param user 要初始化的user对象
     */
    void initGuestUser(User user) ;

    void enableUser(Long id) ;

    void disableUser(Long id) ;

    void deleteUser(Long id);

    List<Map> addBrandUser(String userId);

    List<Map> allNoBelongBrandUser();

}
