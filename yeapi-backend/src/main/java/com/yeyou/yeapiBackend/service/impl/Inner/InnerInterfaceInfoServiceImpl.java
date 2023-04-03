package com.yeyou.yeapiBackend.service.impl.Inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yeyou.yeapiBackend.common.ErrorCode;
import com.yeyou.yeapiBackend.exception.BusinessException;
import com.yeyou.yeapiBackend.mapper.InterfaceInfoMapper;
import com.yeyou.yeapiBackend.service.InterfaceInfoService;
import com.yeyou.yeapicommon.model.entity.InterfaceInfo;
import com.yeyou.yeapicommon.service.InnerInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
* 接口信息RPC调用
*/
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    InterfaceInfoService interfaceInfoService;

    @Override
    public InterfaceInfo getInterfaceInfoById(Long id) {
        if(id==null || id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return interfaceInfoService.getById(id);
    }
}
