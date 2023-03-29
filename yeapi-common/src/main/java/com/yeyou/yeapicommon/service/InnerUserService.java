package com.yeyou.yeapicommon.service;


import com.yeyou.yeapicommon.model.entity.User;

/**
 * 用户信息RPC调用
 */
public interface InnerUserService {
    /**
     * 通过AccessKey获取用户信息
     * @return
     */
    User getInvokeUserInfo(String accessKey);
}
