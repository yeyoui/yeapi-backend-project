package com.yeyou.yeapiBackend.controller;

import com.yeyou.yeapiBackend.annotation.AuthCheck;
import com.yeyou.yeapiBackend.common.BaseResponse;
import com.yeyou.yeapiBackend.common.ErrorCode;
import com.yeyou.yeapiBackend.common.IdRequest;
import com.yeyou.yeapiBackend.common.ResultUtils;
import com.yeyou.yeapiBackend.exception.BusinessException;
import com.yeyou.yeapiBackend.model.dto.interfaceSecret.InterfaceSecretRequest;
import com.yeyou.yeapiBackend.service.InterfaceSecretService;
import com.yeyou.yeapicommon.model.entity.InterfaceSecret;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/interfaceSecret")
public class InterfaceSecretController {
    @Resource
    InterfaceSecretService interfaceSecretService;
    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> addInterfaceSecret(@RequestBody InterfaceSecretRequest interfaceSecretRequest){
        if(interfaceSecretRequest==null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        InterfaceSecret interfaceSecret = new InterfaceSecret();
        BeanUtils.copyProperties(interfaceSecretRequest,interfaceSecret);
        interfaceSecretService.addNewInterfaceSecret(interfaceSecret);
        return ResultUtils.success(interfaceSecret.getInterfaceId());
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> updateInterfaceSecret(@RequestBody InterfaceSecretRequest interfaceSecretRequest){
        if(interfaceSecretRequest==null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        InterfaceSecret interfaceSecret = new InterfaceSecret();
        BeanUtils.copyProperties(interfaceSecretRequest,interfaceSecret);
        interfaceSecretService.updateInterfaceSecret(interfaceSecret);
        return ResultUtils.success(interfaceSecret.getInterfaceId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> delInterfaceSecret(@RequestBody IdRequest idRequest){
        if(idRequest==null) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        interfaceSecretService.delInterfaceSecret(idRequest.getId());
        return ResultUtils.success(idRequest.getId());
    }

    @GetMapping("/list")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<InterfaceSecret>> listAll(){
        return ResultUtils.success(interfaceSecretService.list());
    }

    @GetMapping("/get/{id}")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<InterfaceSecret> getOne(@PathVariable("id") Long id){
        InterfaceSecret interfaceSecret = interfaceSecretService.query().eq("interfaceId", id).one();
        return ResultUtils.success(interfaceSecret);
    }
}
