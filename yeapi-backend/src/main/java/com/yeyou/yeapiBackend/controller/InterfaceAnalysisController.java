package com.yeyou.yeapiBackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yeyou.yeapiBackend.common.BaseResponse;
import com.yeyou.yeapiBackend.common.ErrorCode;
import com.yeyou.yeapiBackend.common.ResultUtils;
import com.yeyou.yeapiBackend.exception.BusinessException;
import com.yeyou.yeapiBackend.mapper.UserInterfaceInfoMapper;
import com.yeyou.yeapiBackend.model.vo.InterfaceInfoVO;
import com.yeyou.yeapiBackend.service.InterfaceInfoService;
import com.yeyou.yeapicommon.model.entity.InterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 接口分析
 * @author yeyou
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class InterfaceAnalysisController {
    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;
    @Resource
    private InterfaceInfoService interfaceInfoService;

    @GetMapping("/top/interface/invoke")
    public BaseResponse<List<InterfaceInfoVO>> topInterfaceInvoke(){
        //1.获取接口id和总调用次数
        List<InterfaceInfoVO> topInvokeInterfaces = userInterfaceInfoMapper.listTopInvokeInterface(5);
        //2.查询每个接口的信息
        Map<Long, List<InterfaceInfoVO>> listMap = topInvokeInterfaces
                .stream()
                .collect(Collectors.groupingBy(InterfaceInfoVO::getInterfaceId));
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", listMap.keySet());
        List<InterfaceInfo> interfaceInfos = interfaceInfoService.list(queryWrapper);
        if(interfaceInfos.isEmpty()){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        //3.封装VO
        List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfos.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
            interfaceInfoVO.setTotalNum(listMap.get(interfaceInfo.getId()).get(0).getTotalNum());
            return interfaceInfoVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(interfaceInfoVOList);
    }
}
