package com.yeyou.yeapicommon.service;

import com.yeyou.yeapicommon.model.entity.InterfaceInfo;

/**
* 接口信息RPC调用
*/
public interface InnerInterfaceInfoService {
    /**
     * 获取接口信息
     */
    InterfaceInfo getInterfaceInfo(String url,String method);
}
