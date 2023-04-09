package com.yeyou.yeapiBackend.model.dto.interfaceSecret;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterfaceSecretRequest implements Serializable {

    private static final long serialVersionUID = -1405176581307979334L;
    /**
     * 主键
     */
    private Long interfaceId;
    /**
     * 秘钥
     */
    private String secret;
}
