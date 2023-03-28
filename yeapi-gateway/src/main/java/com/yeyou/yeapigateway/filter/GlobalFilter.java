package com.yeyou.yeapigateway.filter;

import com.yeyou.yeapiclientsdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class GlobalFilter implements org.springframework.cloud.gateway.filter.GlobalFilter, Ordered {

    @Deprecated
    private static final HashMap<String,String> AKSK=new HashMap<>();
    static {
        AKSK.put("yeyoui","123456");
    }

    private final static List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");
    private final static String INTERFACE_HOST ="localhost:8081";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1. 请求日志
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = INTERFACE_HOST+request.getPath().value();
        String sourceAddress = request.getLocalAddress().getHostString();

        log.info("链路ID: {}",request.getId());
        log.info("调用路径: {}", requestPath);
        log.info("调用参数: {}", request.getQueryParams());
        log.info("方法: {}",request.getMethod());
        log.info("远程地址: {}\n_____________________", sourceAddress);

        ServerHttpResponse response = exchange.getResponse();
        //2. 检查目标ID是否是白名单用户
        if(!IP_WHITE_LIST.contains(sourceAddress)){
            return handleNoAuth(response);
        }
        //3. 用户鉴权
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String clientSign = headers.getFirst("sign");
        String randomNum = headers.getFirst("randomNum");
        String timestamp = headers.getFirst("timestamp");
        String body = headers.getFirst("body");
        //   1. 检查时间戳是否大于5分钟'
        final long FIVE_MINUTES=60 * 5L;
        long nowTimeStamp = System.currentTimeMillis() / 1000;
        if(timestamp==null || nowTimeStamp-Long.parseLong(timestamp)>=FIVE_MINUTES){
            return handleNoAuth(response);
        }
        //   2. todo 在5分钟内随机数是否重新收到（不能收到重复的随机数 使用Redis实现）
        //   3. todo 将数据进行签名运算，然后对比客户端发来的签名
        String secret = AKSK.get(accessKey);
        String serviceSign = SignUtils.getSign(body, secret);
        if(!serviceSign.equals(clientSign)){
            return handleNoAuth(response);
        }
        //5. todo 接口是否存在(RPC调用yeapi-backend的服务)

        //6. 请求转发，调用接口
        chain.filter(exchange);
        //7. 响应日志
        return handleResponse(exchange, chain);
    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 7. todo 调用成功，接口调用次数 + 1 invokeCount
//                                        try {
//                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
//                                        } catch (Exception e) {
//                                            log.error("invokeCount error", e);
//                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }



    private static Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
