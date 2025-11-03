<script setup lang="ts">
import { useAuthStore } from "~/stores/auth";

definePageMeta({
  layout: "auth",
});

const authStore = useAuthStore();
const api = useApi();
const { toast } = useToast();
const router = useRouter();

const formData = reactive({
  account: "",
  password: "",
});
const loading = ref(false);

/** 提交表单 */
const handleSubmit = async () => {
  if (!formData.account || !formData.password) {
    toast({
      variant: "destructive",
      title: "错误",
      description: "请输入账号和密码",
    });
    return;
  }

  loading.value = true;
  try {
    const response = await api.users.login({
      account: formData.account,
      password: formData.password,
    });

    if (response.code === 200 && response.data?.token) {
      authStore.setToken(response.data.token);

      toast({
        title: "成功",
        description: "登录成功",
      });

      // 获取用户信息
      await authStore.fetchUserInfo();

      // 跳转到首页
      router.push("/");
    } else {
      toast({
        variant: "destructive",
        title: "登录失败",
        description: response.message || "账号或密码错误",
      });
    }
  } catch (error: any) {
    toast({
      variant: "destructive",
      title: "登录失败",
      description: error.message || "登录过程中发生错误",
    });
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div
    class="w-full max-w-md space-y-8 p-8 bg-white dark:bg-gray-800 rounded-lg shadow-lg"
  >
    <div class="text-center">
      <h2 class="text-3xl font-bold tracking-tight">Agent-Plus</h2>
      <p class="mt-2 text-sm text-muted-foreground">登录您的账户</p>
    </div>

    <form @submit.prevent="handleSubmit" class="mt-8 space-y-6">
      <div class="space-y-4">
        <div>
          <Label for="account">账号</Label>
          <Input
            id="account"
            v-model="formData.account"
            type="text"
            placeholder="邮箱/手机号"
            :disabled="loading"
            class="mt-1"
          />
        </div>

        <div>
          <Label for="password">密码</Label>
          <Input
            id="password"
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            :disabled="loading"
            class="mt-1"
          />
        </div>
      </div>

      <div class="flex items-center justify-between">
        <NuxtLink
          to="/reset-password"
          class="text-sm text-primary hover:underline"
        >
          忘记密码？
        </NuxtLink>
      </div>

      <Button type="submit" class="w-full" :disabled="loading">
        {{ loading ? "登录中..." : "登录" }}
      </Button>

      <div class="text-center text-sm">
        <span class="text-muted-foreground">还没有账号？</span>
        <NuxtLink to="/register" class="text-primary hover:underline ml-1">
          立即注册
        </NuxtLink>
      </div>
    </form>
  </div>
</template>
