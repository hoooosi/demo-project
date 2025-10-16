import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    // 用户信息
    const userInfo = ref<VO.UserVO | null>(null)
    const isLoggedIn = ref(false)

    // 设置用户信息
    const setUserInfo = (info: VO.UserVO) => {
        userInfo.value = info
        isLoggedIn.value = true
    }

    // 清除用户信息
    const clearUserInfo = () => {
        userInfo.value = null
        isLoggedIn.value = false
        localStorage.removeItem('token')
    }

    // 获取用户显示名称
    const getDisplayName = () => {
        return userInfo.value?.nickName || userInfo.value?.email || '未知用户'
    }

    const localStream = ref<MediaStream | null>(null)

    return {
        userInfo,
        isLoggedIn,
        setUserInfo,
        clearUserInfo,
        getDisplayName,
        localStream
    }
})
