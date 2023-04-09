-- 创建库
create database if not exists yeapi;

-- 切换库
use yeapi;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userName     varchar(256)                           null comment '用户昵称',
    userAccount  varchar(256)                           not null comment '账号',
    userAvatar   varchar(1024)                          null comment '用户头像',
    gender       tinyint                                null comment '性别',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user / admin',
    userPassword varchar(512)                           not null comment '密码',
    accessKey    varchar(64)                            not null comment '访问秘钥',
    secretKey    varchar(64)                            not null comment '密码秘钥',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    constraint uni_userAccount
        unique (userAccount)
) comment '用户';

-- 接口信息
create table interface_info
(
    id             bigint auto_increment primary key comment '主键',
    name           varchar(256)                       not null comment '名称',
    description    varchar(256)                       null comment '描述',
    url            varchar(512)                       not null comment '接口地址',
    requestParams  varchar(512)                       null comment '请求参数(json)',
    requestHeader  text                               null comment '请求头',
    responseHeader text                               null comment '响应头',
    status         int      default 2                 not null comment '接口状态（0-关闭，1-开启 2-等待审核 3-被管理员拒绝）',
    method         varchar(256)                       not null comment '请求类型',
    userId         bigint                             not null comment '创建人ID',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除(0-未删, 1-已删)'
)
    comment '接口信息';

-- 用户调用接口关系表
create table user_interface_info
(
    `id`          bigint auto_increment primary key comment '主键',
    `userId`      bigint                             not null comment '用户ID',
    `interfaceId` bigint                             not null comment '接口ID',
    `status`      int      default 0                 not null comment '状态',
    `totalNum`    int      default 0 comment '总调用次数',
    `surplusNum`  int      default 0 comment '剩余调用次数',
    `createTime`  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime`  datetime default CURRENT_TIMESTAMP not null on update current_timestamp comment '更新时间',
    `isDelete`    tinyint  default 0                 not null comment '是否删除（0-未删  1-以删'
) comment '用户调用接口关系'

-- 接口秘钥表
create table interface_secret
(
    `id`          bigint auto_increment primary key comment '主键',
    `interfaceId` bigint unique     not null comment '接口ID',
    `secret`      varchar(64)       not null comment '接口秘钥',
    `isDelete`    tinyint default 0 not null comment '是否删除（0-未删  1-以删',
    unique index interfaceIdIx (interfaceId)
) comment '接口秘钥表'

