package com.yeyou.yeapiBackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class InterfaceInfoVO implements Serializable {

    private Long interfaceId;

    private String method;
    private String name;
    private String url;
    private Long userId;

    private String description;
    private String requestParams;
    private String requestHeader;
    private String responseHeader;

    private Integer status;
    private Long totalNum;

    private Date createTime;
    private Date updateTime;
    private Integer isDelete;
}
