package com.yeyou.yeapiBackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yeyou.yeapiBackend.mapper")
public class YeApiBackendMain {

    public static void main(String[] args) {
        SpringApplication.run(YeApiBackendMain.class, args);
    }

}
