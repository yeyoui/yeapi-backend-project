package com.yeyou.yeapicommon.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口信息状态
 */
public enum InterfaceInfoEnum {

    OFFLINE("关闭",0),
    ONLINE("上线",1);

    private final String content;
    private final int val;

    InterfaceInfoEnum(String content, int val) {
        this.content = content;
        this.val = val;
    }

    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(InterfaceInfoEnum::getVal).collect(Collectors.toList());
    }


    public String getContent() {
        return content;
    }

    public int getVal() {
        return val;
    }
}
