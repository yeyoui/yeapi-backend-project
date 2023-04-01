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

    /**
     * 根据用户ID和接口ID获取剩余调用次数
     * @param interfaceId
     * @param userId
     * @return
     */
    int getInterfaceSurplusByIUId(long interfaceId, long userId);
}
