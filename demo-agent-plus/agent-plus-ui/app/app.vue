<script setup lang="ts">
import "~/assets/css/main.css";
import Toaster from "~/components/ui/Toaster.vue";
const authStore = useAuthStore();

/** 初始化认证状态 */
onMounted(() => {
  authStore.initAuth();
});
</script>

<template>
  <div>
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
    <Toaster />
  </div>
</template>
