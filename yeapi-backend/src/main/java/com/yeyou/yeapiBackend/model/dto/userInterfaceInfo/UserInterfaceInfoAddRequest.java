package com.yeyou.yeapiBackend.model.dto.userInterfaceInfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口添加请求参数
 *
 */
@Data
public class UserInterfaceInfoAddRequest implements Serializable {

    private static final long serialVersionUID = 2504395398821302448L;
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 接口ID
     */
    private Long interfaceId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer surplusNum;
}
