# Yeapi

## 1.用户管理

### 用户注册

1. 校验账号密码
2. 分配秘钥
3. 添加用户

### 用户登录

1. 校验账号密码
2. 用户信息session存入redis
3. 登录


## 2.API签名认证

### 实现

通过HTTP HEAD传递信息

> 参数1：accessKey：调用标识
>
> 参数2：加密的secretKey：(秘钥不可直接放入请求头)
>
> 参数3：用户请求参数
>
> 参数4：sign
>
> 参数5：随机数（防止重放攻击）
>
> 参数6：时间戳（用于定时清除存储的随机数）

客户端：将用户参数+secretKey作为参数生成不可解密的值（使用签名生成算法，服务器同样使用该方法生成值来判断，可以提取为工具类），然后填充请求头信息

服务端：先检查时间戳是否过期，再检查随机数是否重复，然后才从数据库查询用户的AK SC，通过用户传递的参数，再次生成sign并与客户端传递的sign对比，如果不同则无权限

#### 开发一个简单易用的SDK



## 3.接口管理
### 上线接口

1. 检查传入的参数信息
2. 检查用户信息（只有接口创建者和管理员才能操作）
3. 查找接口是否存在
4. 调用接口以校验接口有效性
5. 更新接口信息

### 下线接口

1. 检查传入的参数信息
2. 检查用户信息（只有接口创建者和管理员才能操作）
3. 查找接口是否存在
4. 调用接口以校验接口有效性
5. 更新接口信息

### 新增接口

1. todo校验信息【接口1】
2. 更新接口

### 修改接口

1. 检查用户权限
2. todo校验信息【接口1】
3. 更新接口

### 删除接口

1. 检查用户权限
2. 删除接口

### 查询接口

1. 分页或者全列表查询
2. 数据脱敏

## 4.接口调用

1. 校验请求参数
2. 校验请求接口状态
3. 获取用户AKSK
4. 调用接口并且返回

## 5.接口调用关系

~~~mysql
-- 用户调用接口关系表
create table user_interface_info
(
    `id`          bigint auto_increment primary key  comment '主键',
    `userId`      bigint                             not null comment '用户ID',
    `interfaceId` bigint                             not null comment '接口ID',
    `status`      int      default 0                 not null comment '状态',
    `totalNum`    int      default 0 comment '总调用次数',
    `surplusNum`  int      default 0 comment '剩余调用次数',
    `createTime`  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime`  datetime default CURRENT_TIMESTAMP not null on update current_timestamp comment '更新时间',
    `isDelete`    tinyint  default 0                 not null comment '是否删除（0-未删  1-以删'
) comment '用户调用接口关系'
~~~

### 管理员调用
1. 接口的增删改查
### 业务需求
1. 用户调用接口时能够自动更新
2. 用户每次调用时，对应接口调用次数加1
