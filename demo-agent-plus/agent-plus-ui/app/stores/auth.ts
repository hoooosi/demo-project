import { defineStore } from 'pinia'
import type { User } from '~/types/api'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: null as User.UserDTO | null,
        token: null as string | null,
        isAuthenticated: false
    }),

    actions: {
        setToken(token: string) {
            this.token = token
            this.isAuthenticated = true
            if (import.meta.client) {
                localStorage.setItem('auth_token', token)
            }
        },

        setUser(user: User.UserDTO) {
            this.user = user
        },

        async logout() {
            this.user = null
            this.token = null
            this.isAuthenticated = false
            if (import.meta.client) {
                localStorage.removeItem('auth_token')
                await navigateTo('/login')
            }
        },

        async fetchUserInfo() {
            try {
                const api = useApi()
                const response = await api.users.getUserInfo()
                if (response.code === 200) {
                    this.setUser(response.data)
                }
            } catch (error) {
                console.error('Failed to fetch user info:', error)
            }
        },

        // 初始化认证状态
        initAuth() {
            if (import.meta.client) {
                const token = localStorage.getItem('auth_token')
                if (token) {
                    this.token = token
                    this.isAuthenticated = true
                }
            }
        }
    }
})
