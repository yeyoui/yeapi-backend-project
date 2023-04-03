package com.yeyou.yeapiBackend.model.dto.interfaceInfo;

import com.yeyou.yeapiBackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 接口查询请求参数
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {

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

    /**
     * 创建人ID
     */
    private Long userId;
}
