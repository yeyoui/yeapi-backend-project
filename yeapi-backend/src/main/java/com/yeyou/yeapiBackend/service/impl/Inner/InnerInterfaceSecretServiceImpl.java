package com.yeyou.yeapiBackend.service.impl.Inner;

import com.yeyou.yeapiBackend.service.InterfaceSecretService;
import com.yeyou.yeapicommon.service.InnerInterfaceSecretService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
* 接口秘钥信息RPC调用
*/
@DubboService
public class InnerInterfaceSecretServiceImpl implements InnerInterfaceSecretService {
    @Resource
    private InterfaceSecretService interfaceSecretService;

    @Override
    public String getSecretByInterfaceId(long interfaceId) {
        return interfaceSecretService.getSecretByInterfaceId(interfaceId);
    }
}
