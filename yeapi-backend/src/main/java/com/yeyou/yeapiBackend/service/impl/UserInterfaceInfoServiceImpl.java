package com.yeyou.yeapiBackend.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeyou.yeapiBackend.common.ErrorCode;
import com.yeyou.yeapiBackend.exception.BusinessException;
import com.yeyou.yeapiBackend.service.UserInterfaceInfoService;
import com.yeyou.yeapiBackend.mapper.UserInterfaceInfoMapper;
import com.yeyou.yeapicommon.model.entity.UserInterfaceInfo;
import org.springframework.stereotype.Service;

/**
* @author lhy
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
* @createDate 2023-03-25 21:09:34
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService{

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        //空参数
        if(userInterfaceInfo==null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        //如果执行的是更新操作
        if(add){
            Long userId = userInterfaceInfo.getUserId();
            Long interfaceId = userInterfaceInfo.getInterfaceId();
            if(userId<0 || interfaceId<0) throw new BusinessException(ErrorCode.PARAMS_ERROR,"错误的ID");
            if(userInterfaceInfo.getSurplusNum()<0)
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于0");
        }

    }

    @Override
    public boolean invokeCount(long interfaceId, long userId) {
        //查询接口和用户参数是否正确
        if(interfaceId<0 || userId<0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceId",interfaceId).eq("userId",userId);
        updateWrapper.gt("surplusNum",0);
        updateWrapper.setSql("totalNum=totalNum+1,surplusNum=surplusNum-1");
        return this.update(updateWrapper);
    }
}




