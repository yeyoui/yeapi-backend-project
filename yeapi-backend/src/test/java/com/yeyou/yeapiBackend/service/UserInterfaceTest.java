package com.yeyou.yeapiBackend.service;

import com.yeyou.yeapiBackend.mapper.UserInterfaceInfoMapper;
import com.yeyou.yeapiBackend.model.vo.InterfaceInfoVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class UserInterfaceTest {
    @Resource
    UserInterfaceInfoService userInterfaceInfoService;
    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Test
    void interfaceIncrOneTest(){
        boolean b = userInterfaceInfoService.invokeCount(1, 1);
        Assertions.assertTrue(b);
    }

    @Test
    void teamMapper(){
        for (InterfaceInfoVO interfaceInfoVO : userInterfaceInfoMapper.listTopInvokeInterface(5)) {
            System.out.println(interfaceInfoVO.getInterfaceId());
            System.out.println(interfaceInfoVO.getTotalNum());
            System.out.println("================");
        }

    }

    @Test
    void testSendMessage(){

    }
}
