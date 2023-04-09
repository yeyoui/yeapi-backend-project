package com.yeyou.yeapiBackend.service;

import com.yeyou.yeapicommon.model.entity.InterfaceSecret;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author lhy
* @description 针对表【interface_secret(接口秘钥表)】的数据库操作Service
* @createDate 2023-04-09 13:59:03
*/
public interface InterfaceSecretService extends IService<InterfaceSecret> {
    /**
     * 新增接口秘钥
     * @param interfaceSecret
     */
    void addNewInterfaceSecret(InterfaceSecret interfaceSecret);
    /**
     * 更新接口秘钥
     * @param interfaceSecret
     */
    void updateInterfaceSecret(InterfaceSecret interfaceSecret);
    /**
     * 删除接口秘钥
     * @param interfaceId
     */
    void delInterfaceSecret(Long interfaceId);

    /**
     * 根据接口ID获取秘钥
     * @param interfaceId
     * @return
     */
    String getSecretByInterfaceId(Long interfaceId);
}
