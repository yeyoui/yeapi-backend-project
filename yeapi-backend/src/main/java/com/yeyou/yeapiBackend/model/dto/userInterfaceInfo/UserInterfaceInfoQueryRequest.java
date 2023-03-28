package com.yeyou.yeapiBackend.model.dto.userInterfaceInfo;

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
public class UserInterfaceInfoQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 471278761377557210L;
    /**
     * 主键
     */
    private Long id;

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
