package com.yeyou.yeapiBackend.model.dto.interfaceInfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口更新请求参数
 *
 */
@Data
public class InterfaceInfoUpdateRequest implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 接口状态（0-关闭，1-开启 2-等待审核 3-被管理员拒绝）
     */
    private Integer status;

    /**
     * 请求类型
     */
    private String method;
}
