import type {
    Result,
    User,
} from '~/types/api'

export const useApi = () => {
    const config = useRuntimeConfig()
    const apiBase = '/api'

    // 创建 fetch 实例
    const fetch = $fetch.create({
        baseURL: apiBase,
        onRequest({ options }) {
            // 添加认证 token
            const token = import.meta.client ? localStorage.getItem('auth_token') : null
            if (token) {
                const headers = new Headers(options.headers);
                headers.set('Authorization', `Bearer ${token}`);
                options.headers = headers;
            }
        },
        async onResponse({ response }) {
            const body = response._data;
            if (body && typeof body.code === 'number') {
                const businessCode = body.code;
                if (businessCode !== 200) {
                    throw createError({
                        statusCode: businessCode,
                        message: body.message || 'API 业务操作失败',
                        data: body,
                    });
                }
            }
        },
        onResponseError({ response }) {
            // 处理 401 未授权
            if (response.status === 401 && import.meta.client) {
                localStorage.removeItem('auth_token')
                navigateTo('/login')
            }
        }
    })


    return {
        users: {
            login: (data: User.LoginRequest) =>
                fetch<Result<User.LoginResponse>>('/login', {
                    method: 'POST',
                    body: data
                }),
            register: (data: User.RegisterRequest) =>
                fetch<Result<any>>('/register', {
                    method: 'POST',
                    body: data
                }),
            getCaptcha: (data?: User.GetCaptchaRequest) =>
                fetch<Result<User.CaptchaResponse>>('/get-captcha', {
                    method: 'POST',
                    body: data || {}
                }),
            resetPassword: (data: User.ResetPasswordRequest) =>
                fetch<Result<any>>('/reset-password', {
                    method: 'POST',
                    body: data
                }),
            sendEmailCode: (data: User.SendEmailCodeRequest) =>
                fetch<Result<any>>('/send-email-code', {
                    method: 'POST',
                    body: data
                }),
            sendResetPasswordCode: (data: User.SendResetPasswordCodeRequest) =>
                fetch<Result<any>>('/send-reset-password-code', {
                    method: 'POST',
                    body: data
                }),
            verifyEmailCode: (data: User.VerifyEmailCodeRequest) =>
                fetch<Result<boolean>>('/verify-email-code', {
                    method: 'POST',
                    body: data
                }),
            getUserInfo: () =>
                fetch<Result<User.UserDTO>>('/users'),
            updateUserInfo: (data: User.UserUpdateRequest) =>
                fetch<Result<any>>('/users', {
                    method: 'POST',
                    body: data
                }),
            updatePassword: (data: User.ChangePasswordRequest) =>
                fetch<Result<any>>('/users/password', {
                    method: 'PUT',
                    body: data
                }),
            getUserSettings: () =>
                fetch<Result<User.UserSettingsDTO>>('/users/settings'),
            updateUserSettings: (data: User.UserSettingsUpdateRequest) =>
                fetch<Result<User.UserSettingsDTO>>('/users/settings', {
                    method: 'PUT',
                    body: data
                }),
            getUserDefaultModelId: () =>
                fetch<Result<number>>('/users/settings/default-model'),
            getOcrModels: () =>
                fetch<Result<User.ModelDTO[]>>('/users/settings/ocr-models'),
            getEmbeddingModels: () =>
                fetch<Result<User.ModelDTO[]>>('/users/settings/embedding-models'),
            getSsoLoginUrl: (provider: string, redirectUrl?: string) =>
                fetch<Result<User.SsoLoginUrlResponse>>(`/sso/${provider}/login`, {
                    params: { redirectUrl }
                }),
            handleSsoCallback: (provider: string, code: string) =>
                fetch<Result<{ token: string }>>(`/sso/${provider}/callback`, {
                    params: { code }
                })
        },
    }
}
