package com.yeyou.yeapiBackend.model.dto.interfaceInfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口调用请求信息
 */
@Data
public class InterfaceInvokeRequest implements Serializable {

    private static final long serialVersionUID = -7962725282923208480L;
    /**
     * 接口ID
     */
    private Long id;

    /**
     * 请求参数
     */
    private String userRequestParams;
}
