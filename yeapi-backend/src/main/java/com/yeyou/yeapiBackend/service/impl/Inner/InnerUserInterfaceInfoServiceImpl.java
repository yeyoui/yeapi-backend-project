package com.yeyou.yeapiBackend.service.impl.Inner;

import com.yeyou.yeapiBackend.service.UserInterfaceInfoService;
import com.yeyou.yeapicommon.model.entity.UserInterfaceInfo;
import com.yeyou.yeapicommon.service.InnerUserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
* 用户接口信息RPC调用
*/
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public boolean invokeCount(long interfaceId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceId,userId);
    }

    @Override
    public int getInterfaceSurplusByIUId(long interfaceId, long userId) {
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.query()
                .select("surplusNum")
                .eq("interfaceId", interfaceId)
                .eq("userId", userId).one();
        if(userInterfaceInfo==null) return 0;
        Integer num=userInterfaceInfo.getSurplusNum();
        if(num==null) return 0;
        return num;
    }
}
