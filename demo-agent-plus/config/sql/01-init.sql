CREATE EXTENSION IF NOT EXISTS vector;

create table
    if not exists public.accounts (
        id bigint primary key not null,
        user_id bigint not null, -- 用户ID
        balance numeric(20, 8) default 0.00000000, -- 账户余额
        credit numeric(20, 8) default 0.00000000, -- 信用额度
        total_consumed numeric(20, 8) default 0.00000000, -- 总消费金额
        last_transaction_at timestamp without time zone, -- 最后交易时间
        created_at bigint, -- 创建时间
        updated_at bigint, -- 更新时间
        deleted_at bigint -- 逻辑删除时间
    );

create table
    if not exists public.users (
        id bigint primary key not null, -- 主键
        nickname character varying(255) not null, -- 昵称
        email character varying(255), -- 邮箱
        phone character varying(11), -- 手机号
        password character varying not null, -- 密码
        is_admin boolean default false,
        login_platform character varying(50),
        github_id character varying(255),
        github_login character varying(255),
        avatar_url character varying(255),
        created_at bigint, -- 创建时间
        updated_at bigint, -- 更新时间
        deleted_at bigint -- 逻辑删除时间
    );

create table
    if not exists public.user_settings (
        id bigint primary key not null, -- 设置记录唯一ID
        user_id bigint not null, -- 用户ID，关联users表
        setting_config json, -- 设置配置JSON，格式：{"default_model": "模型ID"}
        created_at bigint, -- 创建时间
        updated_at bigint, -- 更新时间
        deleted_at bigint -- 逻辑删除时间
    );

create table
    if not exists public.auth_settings (
        id bigint primary key not null, -- 配置记录唯一ID
        feature_type character varying(50) not null, -- 功能类型：LOGIN-登录功能，REGISTER-注册功能
        feature_key character varying(100) not null, -- 功能键：NORMAL_LOGIN, GITHUB_LOGIN, COMMUNITY_LOGIN, USER_REGISTER等
        feature_name character varying(100) not null, -- 功能显示名称
        enabled boolean default true, -- 是否启用该功能
        config_data jsonb, -- 功能配置数据，JSON格式，存储SSO配置等
        display_order integer default 0, -- 显示顺序
        description text, -- 功能描述
        created_at bigint, -- 创建时间
        updated_at bigint, -- 更新时间
        deleted_at bigint -- 逻辑删除时间
    );

create table
    if not exists public.models (
        id bigint primary key not null, -- 模型ID
        user_id bigint not null, -- 用户ID
        provider_id bigint not null, -- 服务提供商ID
        model_id character varying(100) not null, -- 模型ID标识
        name character varying(100) not null, -- 模型名称
        model_endpoint character varying(255) not null,
        description text, -- 模型描述
        is_official boolean default false, -- 是否官方模型
        type character varying(20) not null, -- 模型类型
        status boolean default true, -- 模型状态
        created_at bigint, -- 创建时间
        updated_at bigint, -- 更新时间
        deleted_at bigint -- 逻辑删除时间
    );

create table
    if not exists public.providers (
        id bigint primary key not null, -- 服务提供商ID
        user_id bigint not null, -- 用户ID
        protocol character varying(50) not null, -- 协议类型
        name character varying(100) not null, -- 服务提供商名称
        description text, -- 服务提供商描述
        config text, -- 服务提供商配置,加密后的值
        is_official boolean default false, -- 是否官方服务提供商
        status boolean default true, -- 服务提供商状态
        created_at bigint, -- 创建时间
        updated_at bigint, -- 更新时间
        deleted_at bigint -- 逻辑删除时间
    );