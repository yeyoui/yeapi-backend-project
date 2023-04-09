package com.yeyou.yeapiBackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeyou.yeapiBackend.common.ErrorCode;
import com.yeyou.yeapiBackend.exception.BusinessException;
import com.yeyou.yeapiBackend.service.InterfaceInfoService;
import com.yeyou.yeapiBackend.mapper.InterfaceInfoMapper;
import com.yeyou.yeapicommon.model.entity.InterfaceInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
* @author lhy
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2023-03-21 16:40:37
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService{

    private static final List<String> METHOD_LIST=Arrays.asList("GET","POST","PUT","DELETE");

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        String description = interfaceInfo.getDescription();
        String url = interfaceInfo.getUrl();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        String method = interfaceInfo.getMethod();

        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name, description, url, requestHeader, responseHeader,method)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > 256) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口名称过长");
        }
        if(StringUtils.isNotBlank(url) && description.length()>512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口地址过长");
        }
        if(StringUtils.isNotBlank(description) && description.length()>512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口描述过长");
        }
        if(StringUtils.isNotBlank(method) && !METHOD_LIST.contains(method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口的请求方法有误");
        }
    }
}




