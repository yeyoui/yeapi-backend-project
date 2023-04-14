package com.yeyou.yeapiBackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeyou.yeapiBackend.annotation.AuthCheck;
import com.yeyou.yeapiBackend.common.*;
import com.yeyou.yeapiBackend.constant.CommonConstant;
import com.yeyou.yeapiBackend.exception.BusinessException;
import com.yeyou.yeapiBackend.model.dto.interfaceInfo.InterfaceInfoAddRequest;
import com.yeyou.yeapiBackend.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.yeyou.yeapiBackend.model.dto.interfaceInfo.InterfaceInfoUpdateRequest;
import com.yeyou.yeapiBackend.model.dto.interfaceInfo.InterfaceInvokeRequest;
import com.yeyou.yeapiBackend.model.enums.InterfaceInfoEnum;
import com.yeyou.yeapiBackend.service.InterfaceInfoService;
import com.yeyou.yeapiBackend.service.UserService;
import com.yeyou.yeapiclientsdk.client.YeApiClient;
import com.yeyou.yeapiclientsdk.utils.CustomizeInvokeUtils;
import com.yeyou.yeapicommon.model.entity.InterfaceInfo;
import com.yeyou.yeapicommon.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 接口信息管理
 *
 * @author yeyou
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private YeApiClient yeApiClient;

    @Value("${gatewayhost}")
    private String GATEWAY_HOST;



    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(anyRole = {"admin","dev"})
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(anyRole = {"admin","dev"})
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(anyRole = {"admin","dev"})
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                            HttpServletRequest request) {
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(long id,HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        boolean isAdmin = userService.isAdmin(request);
        if(!isAdmin){
            //接口地址脱敏
            Pattern pattern = Pattern.compile("^https?://.*?/");
            String url = interfaceInfo.getUrl();
            Matcher matcher = pattern.matcher(url);
            String urlVo;
            if (matcher.find(0)) {
                String val = matcher.group(0);
                urlVo = GATEWAY_HOST + "/" + url.substring(val.length());
            } else {
                urlVo = GATEWAY_HOST;
            }
            interfaceInfo.setUrl(urlVo);
        }
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // description 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        boolean isAdmin = userService.isAdmin(request);
        if(!isAdmin){
            //接口地址脱敏
            Pattern pattern = Pattern.compile("^https?://.*?/");
            interfaceInfoPage.getRecords().forEach((item)->{
                String url = item.getUrl();
                Matcher matcher = pattern.matcher(url);
                String urlVo;
                if(matcher.find(0)){
                    String val = matcher.group(0);
                    urlVo = GATEWAY_HOST + "/" + url.substring(val.length());
                }else {
                    urlVo=GATEWAY_HOST;
                }
                item.setUrl(urlVo);
            });
        }
        return ResultUtils.success(interfaceInfoPage);
    }

    /**
     * 接口上线
     */
    @PostMapping("/online")
    @AuthCheck(anyRole = {"admin","dev"})
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest, HttpServletRequest request){
        //1. 检查传入的参数信息
        if(idRequest==null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Long interfaceId = idRequest.getId();
        if(interfaceId==null || interfaceId<0) throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口ID信息错误");
        //2. 查找接口是否存在
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        if(interfaceInfo==null) throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口不存在");
        //3. 检查用户信息（只有接口创建者和管理员才能操作）
        User loginUser = userService.getLoginUser(request);
        if(!interfaceInfo.getUserId().equals(loginUser.getId())&& !userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权操作");
        }
        //4. 调用接口以校验接口有效性
//        String result = yeApiClient.getUserByPost(new com.yeyou.yeapiclientsdk.model.User("yeyoui",123,new Pet("hhh")));
//        if(StringUtils.isBlank(result)){
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统接口验证失败");
//        }
        //5. 更新接口信息
        InterfaceInfo newInterface = new InterfaceInfo();
        newInterface.setId(interfaceId);
        newInterface.setStatus(InterfaceInfoEnum.ONLINE.getVal());

        boolean isSucceed = interfaceInfoService.updateById(newInterface);
        if(!isSucceed){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口更新失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 接口下线
     */
    @PostMapping("/offline")
    @AuthCheck(anyRole = {"admin","dev"})
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest, HttpServletRequest request){
        //1. 检查传入的参数信息
        if(idRequest==null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Long interfaceId = idRequest.getId();
        if(interfaceId==null || interfaceId<0) throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口ID信息错误");
        //2. 查找接口是否存在
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        if(interfaceInfo==null) throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口不存在");
        //3. 检查用户信息（只有接口创建者和管理员才能操作）
        User loginUser = userService.getLoginUser(request);
        if(!interfaceInfo.getUserId().equals(loginUser.getId())&& !userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权操作");
        }
        //4. 更新接口信息
        InterfaceInfo newInterface = new InterfaceInfo();
        newInterface.setId(interfaceId);
        newInterface.setStatus(InterfaceInfoEnum.OFFLINE.getVal());

        boolean isSucceed = interfaceInfoService.updateById(newInterface);
        if(!isSucceed){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口更新失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 模拟调用
     * @param invokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse invokeInterface(@RequestBody InterfaceInvokeRequest invokeRequest, HttpServletRequest request){
        //1. 检查传入的参数信息
        if(invokeRequest==null || invokeRequest.getId()<=0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        Long interfaceId = invokeRequest.getId();
        //2. 查找接口是否存在
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        if(interfaceInfo==null) throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口不存在");
        //3. 获取用户信息
        User loginUser = userService.getLoginUser(request);
        if(loginUser==null) throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        //4. 调用接口以校验接口有效性
//        String result = yeApiClient.getUserByPost(new com.yeyou.yeapiclientsdk.model.User("yeyoui"));
//        if(StringUtils.isBlank(result)){
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统接口验证失败");
//        }
        //后端能直接处理
        if(interfaceId==4){
            return ResultUtils.success(request.getRemoteAddr());
        }
        //5.获取AKSK并且调用接口
        String accessKey= loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();
        YeApiClient userYeApiClient = new YeApiClient(accessKey,secretKey);
        //6.获取要模拟调用方法的必备信息
        Object callResult=null;
        try {
            callResult =CustomizeInvokeUtils.invokeYeApiClientMethod(userYeApiClient, interfaceInfo.getName(), invokeRequest.getUserRequestParams());
        } catch (Exception e) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "无法请求接口调用");
        }
        return ResultUtils.success(callResult);
    }

    /**
     * 同意用户上传接口（管理员权限）
     * @param idRequest
     * @return
     */
    @GetMapping("/agreeUpload")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> agreeUpload(IdRequest idRequest){
        if(idRequest.getId()<=0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        boolean result = interfaceInfoService.update().set("status", 1).update();
        if(!result) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(true);
    }

}
