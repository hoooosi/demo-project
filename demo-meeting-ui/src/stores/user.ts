import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    // User information
    const userInfo = ref<VO.UserVO | null>(null)
    const isLoggedIn = ref(false)

    // Set user information
    const setUserInfo = (info: VO.UserVO) => {
        userInfo.value = info
        isLoggedIn.value = true
    }

    // Clear user information
    const clearUserInfo = () => {
        userInfo.value = null
        isLoggedIn.value = false
        localStorage.removeItem('token')
    }

    // Get user display name
    const getDisplayName = () => {
        return userInfo.value?.nickName || userInfo.value?.email || 'Unknown User'
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
