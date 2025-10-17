<script setup lang="ts">
import { RouterView } from 'vue-router'
import { onMounted, watchEffect } from 'vue'
import { useUserStore } from '@/stores/user'
import { wsManager } from '@/utils/websocket'
import { UserApi } from './api'

const userStore = useUserStore()

watchEffect(() => {
  const userInfo = userStore.userInfo
  if (!userInfo) wsManager.disconnect()
  else {
    const token = localStorage.getItem('token')
    if (token) {
      wsManager.connect({
        token,
        hostname: window.location.hostname,
        port: 8089,
      })
    }
  }
})

onMounted(async () => {
  const token = localStorage.getItem('token')
  if (token) {
    const res = await UserApi.me()
    userStore.setUserInfo(res.data)
    await wsManager.connect({
      token,
      hostname: window.location.hostname,
      port: 8089,
    })
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
