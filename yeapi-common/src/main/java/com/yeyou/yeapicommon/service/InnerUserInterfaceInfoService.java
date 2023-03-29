package com.yeyou.yeapicommon.service;

/**
* 用户接口信息RPC调用
*/
public interface InnerUserInterfaceInfoService {

    /**
     * 接口调用计数
     * @param interfaceId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceId,long userId);
}
