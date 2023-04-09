package com.yeyou.yeapiBackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
 * @author yeyou
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1315561556865461424L;
}
