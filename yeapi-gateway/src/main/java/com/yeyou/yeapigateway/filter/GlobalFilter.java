package com.yeyou.yeapigateway.filter;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.Digester;
import com.yeyou.yeapiclientsdk.utils.SignUtils;
import com.yeyou.yeapicommon.model.entity.InterfaceInfo;
import com.yeyou.yeapicommon.model.entity.User;
import com.yeyou.yeapicommon.service.InnerInterfaceInfoService;
import com.yeyou.yeapicommon.service.InnerInterfaceSecretService;
import com.yeyou.yeapicommon.service.InnerUserInterfaceInfoService;
import com.yeyou.yeapicommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.yeyou.yeapigateway.constants.RedisConstants.USER_RANDOM_NUM_EXPIRE_MIN;
import static com.yeyou.yeapigateway.constants.RedisConstants.USER_RANDOM_NUM_KEY_PRE;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Component
@Slf4j
public class GlobalFilter implements org.springframework.cloud.gateway.filter.GlobalFilter, Ordered {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @DubboReference
    private InnerUserService innerUserService;
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;
    @DubboReference
    private InnerInterfaceSecretService innerInterfaceSecretService;

    private final static List<String> IP_BLACK_LIST = Arrays.asList("null");
    private final static String GATEWAY_NAME="yeapi.gateway";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1. 请求日志
        ServerHttpRequest request = exchange.getRequest();
//        String requestPath = INTERFACE_HOST+request.getPath().value();
        String sourceAddress = request.getRemoteAddress().getHostString();
        String method = request.getMethod().toString();
        ServerHttpResponse response = exchange.getResponse();

        //3. 用户鉴权
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String clientSign = headers.getFirst("sign");
        String randomNum = headers.getFirst("randomNum");
        String timestamp = headers.getFirst("timestamp");
        String body = headers.getFirst("body");
        String interfaceId = headers.getFirst("interfaceId");
        String clientIP = headers.getFirst("X-Forwarded-For");
        if(clientIP==null) clientIP="null";
        else clientIP=clientIP.split(",")[0];

        //2. 检查目标IP是否是白名单用户
//        if(IP_BLACK_LIST.contains(clientIP)){
//            return handleNoAuth(response);
//        }

        log.info("链路ID: {}",request.getId());
        log.info("调用路径: {}", request.getPath());
        log.info("调用参数: {}", request.getQueryParams());
        log.info("方法: {}", method);
        log.info("远程地址: {}", clientIP);

        User invokeUserInfo = null;
        invokeUserInfo = innerUserService.getInvokeUserInfo(accessKey);
        if(invokeUserInfo==null){
            log.info("秘钥错误");
            return handleNoAuth(response);
        }
        //3.1将数据进行签名运算，然后对比客户端发来的签名
        String serviceSign = SignUtils.getSign(body, invokeUserInfo.getSecretKey());
        //秘钥错误
        if(!serviceSign.equals(clientSign)){
            log.info("秘钥错误");
            return handleNoAuth(response);
        }

        //4. 接口是否存在(RPC调用yeapi-backend的服务)
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfoById(Long.parseLong(interfaceId));
        } catch (Exception e) {
            log.error("getInterface error",e);
        }
        if(interfaceInfo==null) {
            log.info("找不到接口");
            return handleArgsErr(response);
        }

        //5.预防重放攻击
        //5.1 检查时间戳是否大于5分钟'
        final long FIVE_MINUTES=60 * 5L;
        long nowTimeStamp = System.currentTimeMillis() / 1000;
        if(timestamp==null || nowTimeStamp-Long.parseLong(timestamp)>=FIVE_MINUTES){
            return handleNoAuth(response);
        }
        //5.2 在5分钟内随机数是否重新收到（不能收到重复的随机数 使用Redis实现，防止重放攻击）
        String key=USER_RANDOM_NUM_KEY_PRE+interfaceInfo.getId()+":"+invokeUserInfo.getId()+randomNum;
        String isExisted = stringRedisTemplate.opsForValue().get(key);
        if(isExisted!=null){
            log.info("重复的随机数");
            return handleArgsErr(response);
        }
        //暂存随机数
        stringRedisTemplate.opsForValue().set(key,randomNum,USER_RANDOM_NUM_EXPIRE_MIN, TimeUnit.MINUTES);

        //5.3 用户还有调用次数
        int surplus = innerUserInterfaceInfoService.getInterfaceSurplusByIUId(interfaceInfo.getId(), invokeUserInfo.getId());
        if(surplus<=0) return handleNoAuth(response);

        //流量染色
        String secret = innerInterfaceSecretService.getSecretByInterfaceId(interfaceInfo.getId());
        String gateWaySign=null;
        if(secret!=null){
            Digester digester = new Digester(DigestAlgorithm.SHA256);
            gateWaySign=digester.digestHex(GATEWAY_NAME+secret);
        }

        //6.1匹配请求地址
        String uriStr = interfaceInfo.getUrl();
        String query = request.getURI().getQuery();
        URI uri = UriComponentsBuilder.fromHttpUrl(uriStr).query(query).build().toUri();
        // 生成新的Request对象，该对象放弃了常规路由配置中的spring.cloud.gateway.routes.uri字段
        ServerHttpRequest newRequest = request
                .mutate()
                .header("gatewayInfo",gateWaySign)
                .header("clientIP",clientIP)
                .method(request.getMethod())
                .uri(uri)
                .build();
        // 取出当前的route对象
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        //重新设置Route地址
        Route newRoute =
                Route.async().asyncPredicate(route.getPredicate()).filters(route.getFilters()).id(route.getId())
                        .order(route.getOrder()).uri(uri).build();
        // 放回exchange中
        exchange.getAttributes().put(GATEWAY_ROUTE_ATTR,newRoute);
        //6. 请求转发，调用接口
//        boolean result = innerUserInterfaceInfoService.invokeCount(interfaceInfo.getId(), invokeUserInfo.getId());
//        return chain.filter(exchange.mutate().request(newRequest).build());
        //7. 响应日志
        return handleResponse(exchange.mutate().request(newRequest).build(), chain,interfaceInfo.getId(), invokeUserInfo.getId());
    }



    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain,long interfaceInfoId,long userId) {
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
                                        // 7. 调用成功，接口调用次数 + 1 invokeCount
                                        try {
                                            boolean result = innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
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

    private static Mono<Void> handleArgsErr(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
