package com.yeyou.yeapiBackend.constant;

/**
 * Redis常用的键
 */
public interface RedisConstant {
    String LOGIN_CODE_KEY="user:emailCode:";
    Long LOGIN_CODE_TLL=5L;
}
