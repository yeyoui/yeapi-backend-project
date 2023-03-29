package com.yeyou.yeapigateway;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.stereotype.Service;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDubbo
@Service
public class YeapiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(YeapiGatewayApplication.class, args);
    }

}
