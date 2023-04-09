package com.yeyou.yeapicommon.service;
/**
 * 接口秘钥信息RPC调用
 */
public interface InnerInterfaceSecretService {
    /**
     * 根据接口ID获取接口秘钥
     * @param interfaceId
     * @return
     */
    String getSecretByInterfaceId(long interfaceId);
}
