package com.yeyou.yeapiBackend.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeyou.yeapiBackend.annotation.AuthCheck;
import com.yeyou.yeapiBackend.common.ErrorCode;
import com.yeyou.yeapiBackend.exception.BusinessException;
import com.yeyou.yeapiBackend.service.InterfaceInfoService;
import com.yeyou.yeapiBackend.service.InterfaceSecretService;
import com.yeyou.yeapicommon.model.entity.InterfaceSecret;
import com.yeyou.yeapiBackend.mapper.InterfaceSecretMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author lhy
* @description 针对表【interface_secret(接口秘钥表)】的数据库操作Service实现
* @createDate 2023-04-09 13:59:03
*/
@Service
public class InterfaceSecretServiceImpl extends ServiceImpl<InterfaceSecretMapper, InterfaceSecret>
    implements InterfaceSecretService {
    @Resource
    private InterfaceInfoService interfaceInfoService;
    @Override
    public void addNewInterfaceSecret(InterfaceSecret interfaceSecret) {
        //参数校验

        if(interfaceSecret.getInterfaceId() <0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口ID错误");
        }
        if(StringUtils.isBlank(interfaceSecret.getSecret())) {
            //秘钥为空自动添加随机秘钥
            String secret = UUID.randomUUID().toString();
            interfaceSecret.setSecret(secret);
        }
        Long count = interfaceInfoService.query().eq("id", interfaceSecret.getInterfaceId()).count();
        if(count==0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口不存在");
        }

        count = this.query().eq("interfaceId", interfaceSecret.getInterfaceId()).count();
        if(count!=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "秘钥已存在，请调用更新方法");
        }
        //保存信息
        boolean result = this.save(interfaceSecret);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    @Override
    public void updateInterfaceSecret(InterfaceSecret interfaceSecret) {
        //参数校验
        Long interfaceId=interfaceSecret.getInterfaceId();
        String secret=interfaceSecret.getSecret();
        if(interfaceId <0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口ID错误");
        }
        if(StringUtils.isBlank(secret)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "秘钥为空");
        }
        Long count = interfaceInfoService.query().eq("interfaceId", interfaceSecret.getInterfaceId()).count();
        if(count==0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口不存在");
        }
        count = interfaceInfoService.query().eq("interfaceId", interfaceSecret.getInterfaceId()).count();
        if(count==0){//接口秘钥信息不存在
            this.addNewInterfaceSecret(interfaceSecret);
            return;
        }
        //保存信息
        boolean result = this.update()
                .set("secret",interfaceId)
                .eq("interfaceId",secret)
                .update();
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    @Override
    public void delInterfaceSecret(Long interfaceId) {
        if(interfaceId <0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口ID错误");
        }
        boolean result = this.update().eq("interfaceId", interfaceId).remove();
        if(!result){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口不存在");
        }
    }

    @Override
    public String getSecretByInterfaceId(Long interfaceId) {
        InterfaceSecret interfaceSecret = this.query().eq("interfaceId", interfaceId).one();
        return interfaceSecret==null?null: interfaceSecret.getSecret();
    }
}




