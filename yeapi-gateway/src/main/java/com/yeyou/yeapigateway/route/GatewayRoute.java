package com.yeyou.yeapigateway.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoute {
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r->r.path("/yeapi/**").uri("http://localhost:1111")).build();
    }
}
