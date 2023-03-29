package com.yeyou.yeapiBackend.service.impl.Inner;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yeyou.yeapiBackend.common.ErrorCode;
import com.yeyou.yeapiBackend.exception.BusinessException;
import com.yeyou.yeapiBackend.mapper.UserMapper;
import com.yeyou.yeapicommon.model.entity.User;
import com.yeyou.yeapicommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 用户信息RPC调用
 */
@DubboService
@Slf4j
public class InnerUserServiceImpl implements InnerUserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public User getInvokeUserInfo(String accessKey) {
        if(StringUtils.isBlank(accessKey)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);
        return userMapper.selectOne(queryWrapper);
    }
}
