package com.yeyou.yeapiBackend.service.impl.Inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yeyou.yeapiBackend.common.ErrorCode;
import com.yeyou.yeapiBackend.exception.BusinessException;
import com.yeyou.yeapiBackend.mapper.InterfaceInfoMapper;
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
    InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if(StringUtils.isAnyBlank(url,method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", url).eq("method", method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }
}
