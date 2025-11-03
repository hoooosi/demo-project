
insert into public.auth_settings (
    id,
    feature_type,
    feature_key,
    feature_name,
    enabled,
    config_data,
    display_order,
    description
)
values
(
    -- 普通登录 (NORMAL_LOGIN)
    1, 
    'LOGIN',
    'NORMAL_LOGIN',
    '普通登录',
    TRUE,
    '{}'::jsonb,
    10,
    '用户通过用户名/密码进行登录的基础功能'
),
(
    -- GitHub 登录 (GITHUB_LOGIN)
    2, 
    'LOGIN',
    'GITHUB_LOGIN',
    'GitHub登录',
    FALSE, -- 默认SSO配置是未启用的，需要管理员手动配置
    '{}'::jsonb,
    20,
    '通过 GitHub OAuth 进行登录'
),
(
    -- 用户注册 (USER_REGISTER)
    3, 
   'LOGIN',
    'USER_REGISTER',
    '用户注册',
    TRUE, -- 默认用户注册是启用的
    '{}'::jsonb,
    10,
    '允许新用户进行自助注册'
);