<script setup lang="ts">
import { RouterView } from 'vue-router'
import { watch, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { wsManager } from '@/utils/websocket'
import { lo } from 'element-plus/es/locales.mjs'
import { UserApi } from './api'

const userStore = useUserStore()

onMounted(async () => {
  const token = localStorage.getItem('token')

  if (token) {
    if (!token) {
      console.warn('[App] No token found, skipping WebSocket connection')
      return
    }
    const res = await UserApi.me()
    userStore.setUserInfo(res.data)
    try {
      await wsManager.connect({
        token,
        hostname: window.location.hostname,
        port: 8089,
      })
      console.log('[App] WebSocket connected successfully')
    } catch (error) {
      console.error('[App] Failed to connect WebSocket:', error)
    }
  } else {
    console.log('[App] User info cleared, disconnecting WebSocket...')
    wsManager.disconnect()
  }
})
</script>

<template>
  <RouterView />
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

#app {
  width: 100%;
  height: 100vh;
}
</style>
