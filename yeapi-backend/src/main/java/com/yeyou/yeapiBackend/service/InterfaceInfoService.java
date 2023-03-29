package com.yeyou.yeapiBackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeyou.yeapicommon.model.entity.InterfaceInfo;

/**
* @author lhy
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-03-21 16:40:37
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    /**
     * 校验
     * @param interfaceInfo
     * @param add 是否为创建校验
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
