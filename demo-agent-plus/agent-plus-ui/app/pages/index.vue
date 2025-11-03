<script setup lang="ts">
import { useAuthStore } from "~/stores/auth";
import Button from "~/components/ui/Button.vue";

const authStore = useAuthStore();

// 如果已登录，跳转到首页
if (authStore.isAuthenticated) {
  navigateTo("/");
}
</script>

<template>
  <div
    class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800"
  >
    <div class="text-center space-y-8 p-8">
      <div>
        <h1 class="text-6xl font-bold text-primary mb-4">Agent-Plus</h1>
        <p class="text-xl text-muted-foreground">您的全方位 AI 代理平台</p>
      </div>

      <div class="flex gap-4 justify-center">
        <NuxtLink to="/login">
          <Button size="lg">登录</Button>
        </NuxtLink>
        <NuxtLink to="/register">
          <Button variant="outline" size="lg">注册</Button>
        </NuxtLink>
      </div>
    </div>
  </div>
</template>
