package com.yeyou.yeapiBackend.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class UserInterfaceTest {
    @Resource
    UserInterfaceInfoService userInterfaceInfoService;

    @Test
    void interfaceIncrOneTest(){
        boolean b = userInterfaceInfoService.invokeCount(1, 1);
        Assertions.assertTrue(b);
    }

    @Test
    void testSendMessage(){

    }
}
