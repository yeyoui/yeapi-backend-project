package com.yeyou.yeapiBackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeyou.yeapiBackend.model.vo.InterfaceInfoVO;
import com.yeyou.yeapicommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
* @author lhy
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
* @createDate 2023-03-25 21:09:34
* @Entity com.yeyou.yeapiBackend.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {
    List<InterfaceInfoVO> listTopInvokeInterface(int num);

}




