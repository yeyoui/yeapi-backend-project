package com.yeyou.yeapiBackend.service;

import com.yeyou.yeapiBackend.model.entity.InterfaceInfo;
import com.yeyou.yeapiBackend.model.entity.UserInterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author lhy
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2023-03-25 21:09:34
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    /**
     * 校验
     * @param userInterfaceInfo
     * @param add 是否为创建校验
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 接口调用计数
     * @param interfaceId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceId,long userId);
}
