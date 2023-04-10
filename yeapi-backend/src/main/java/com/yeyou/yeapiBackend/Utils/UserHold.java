package com.yeyou.yeapiBackend.Utils;

import com.yeyou.yeapicommon.model.entity.User;

/**
 * 使用ThreadLocal存储当前会话的用户信息
 */
public class UserHold {
    private static final ThreadLocal<User> userInfo=new ThreadLocal<>();

    public static User get(){
        return userInfo.get();
    }

    public static void set(User user){
        userInfo.set(user);
    }

    public static void remove(){
        userInfo.remove();
    }
}
