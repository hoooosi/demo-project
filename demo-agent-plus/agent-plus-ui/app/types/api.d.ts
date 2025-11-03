// API 响应通用类型
export interface Result<T = any> {
    code: number
    message: string
    data: T
    timestamp: number
}

export namespace User {
    // 登录请求
    export interface LoginRequest {
        account: string
        password: string
    }

    // 登录响应
    export interface LoginResponse {
        token: string
        [key: string]: any
    }

    // 注册请求
    export interface RegisterRequest {
        email?: string
        phone?: string
        password: string
        code: string
    }

    // 验证码请求
    export interface GetCaptchaRequest { }

    // 验证码响应
    export interface CaptchaResponse {
        uuid: string
        imageBase64: string
    }

    // 发送邮箱验证码请求
    export interface SendEmailCodeRequest {
        email: string
        captchaUuid: string
        captchaCode: string
    }

    // 验证邮箱验证码请求
    export interface VerifyEmailCodeRequest {
        email: string
        code: string
    }

    // 重置密码请求
    export interface SendResetPasswordCodeRequest {
        email: string
        captchaUuid: string
        captchaCode: string
    }

    export interface ResetPasswordRequest {
        email: string
        newPassword: string
        code: string
    }

    // 用户信息
    export interface UserDTO {
        id: number
        email?: string
        phone?: string
        nickname?: string
        avatar?: string
        createdAt?: number
        updatedAt?: number
    }

    // 修改密码请求
    export interface ChangePasswordRequest {
        currentPassword: string
        newPassword: string
        confirmPassword: string
    }

    // 更新用户信息请求
    export interface UserUpdateRequest {
        nickname: string
    }    // SSO 登录
    export interface SsoLoginUrlResponse {
        url: string
        [key: string]: string
    }

    // 用户设置配置
    export interface FallbackConfig {
        enabled: boolean
        fallbackChain: string[]
    }

    export interface UserSettingsConfig {
        defaultModel?: number
        defaultOcrModel?: number
        defaultEmbeddingModel?: number
        fallbackConfig?: FallbackConfig
    }

    export interface UserSettingsDTO {
        id?: number
        userId?: number
        settingConfig?: UserSettingsConfig
        createdAt?: number
        updatedAt?: number
    }

    export interface UserSettingsUpdateRequest {
        settingConfig: UserSettingsConfig
    }

    // 模型信息
    export interface ModelDTO {
        id: number
        userId?: number
        providerId?: number
        providerName?: string
        modelId: string
        name: string
        description?: string
        type: 'CHAT' | 'EMBEDDING'
        modelEndpoint?: string
        isOfficial?: boolean
        status?: boolean
        createdAt?: number
        updatedAt?: number
    }

}
