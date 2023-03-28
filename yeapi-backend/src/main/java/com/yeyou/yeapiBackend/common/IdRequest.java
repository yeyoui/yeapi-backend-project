package com.yeyou.yeapiBackend.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class IdRequest implements Serializable {

    private static final long serialVersionUID = 4599289395729403321L;
    private Long id;
}
