package com.yeyou.yeapiBackend.service;

import com.yeyou.yeapiclientsdk.utils.ReflectionUtils;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ReflectionInvokeTest {
    @Test
    public void testGson() {
        String s = "[{\"type\":\"String\",\"value\":\"yeyouii\"}\n{\"type\":\"Double\",\"value\":\"123.2132\"}]";
        for (Pair<Class<?>, String> pair : ReflectionUtils.getBasicClassTypeAndValueByJson(s)) {
            System.out.println("Type: "+pair.getKey()+" Value: "+pair.getValue());
        }
    }
}
