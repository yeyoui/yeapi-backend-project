package com.yeyou.yeapicommon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口秘钥表
 * @TableName interface_secret
 */
@TableName(value ="interface_secret")
@Data
public class InterfaceSecret implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接口ID
     */
    private Long interfaceId;

    /**
     * 接口秘钥
     */
    private String secret;

    /**
     * 是否删除（0-未删  1-以删
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
