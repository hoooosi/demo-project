<script setup lang="ts">
definePageMeta({
  layout: "auth",
});

const { toast } = useToast();
const { users } = useApi();
const router = useRouter();

const step = ref(1); // 1: 发送验证码, 2: 重置密码
const captchaDialogShow = ref(false);
const loading = ref(false);
const codeSending = ref(false);
const countdown = ref(0);

const formData = reactive({
  email: "",
  code: "",
  newPassword: "",
  confirmPassword: "",
  captchaCode: "",
  captchaUUID: "",
});

/** 倒计时逻辑 */
watch(countdown, (val) => {
  if (val > 0) {
    setTimeout(() => {
      countdown.value = val - 1;
    }, 1000);
  }
});

/** 监听对话框关闭，发送验证码 */
watch(captchaDialogShow, async (newVal, oldVal) => {
  // 对话框从打开变为关闭，且已填写验证码，则发送邮箱验证码
  if (oldVal && !newVal && formData.captchaCode && formData.captchaUUID) {
    await sendResetPasswordCode();
  }
});

/** 验证邮箱格式 */
const validateEmail = (email: string): boolean => {
  if (!email) {
    toast({
      variant: "destructive",
      title: "错误",
      description: "请输入邮箱",
    });
    return false;
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    toast({
      variant: "destructive",
      title: "错误",
      description: "请输入有效的邮箱地址",
    });
    return false;
  }

  return true;
};

/** 点击发送验证码按钮 */
const handleSendCode = () => {
  if (!validateEmail(formData.email)) {
    return;
  }
  // 清空之前的验证码
  formData.captchaCode = "";
  formData.captchaUUID = "";
  // 打开图形验证码对话框
  captchaDialogShow.value = true;
};

/** 发送重置密码验证码 */
const sendResetPasswordCode = async () => {
  codeSending.value = true;
  try {
    const response = await users.sendResetPasswordCode({
      email: formData.email,
      captchaUuid: formData.captchaUUID,
      captchaCode: formData.captchaCode,
    });

    if (response.code === 200) {
      toast({
        title: "成功",
        description: "验证码已发送到您的邮箱",
      });
      countdown.value = 60;
      step.value = 2; // 进入下一步
    } else {
      toast({
        variant: "destructive",
        title: "发送失败",
        description: response.message || "发送验证码失败",
      });
      // 发送失败，清空验证码，允许重新输入
      formData.captchaCode = "";
      formData.captchaUUID = "";
    }
  } catch (error: any) {
    toast({
      variant: "destructive",
      title: "发送失败",
      description: error.data?.message || "发送验证码失败",
    });
    // 发送失败，清空验证码，允许重新输入
    formData.captchaCode = "";
    formData.captchaUUID = "";
  } finally {
    codeSending.value = false;
  }
};

/** 提交重置密码 */
const handleSubmit = async () => {
  if (!formData.code) {
    toast({
      variant: "destructive",
      title: "错误",
      description: "请输入验证码",
    });
    return;
  }

  if (!formData.newPassword) {
    toast({
      variant: "destructive",
      title: "错误",
      description: "请输入新密码",
    });
    return;
  }

  if (formData.newPassword.length < 6 || formData.newPassword.length > 20) {
    toast({
      variant: "destructive",
      title: "错误",
      description: "密码长度应为6-20个字符",
    });
    return;
  }

  if (formData.newPassword !== formData.confirmPassword) {
    toast({
      variant: "destructive",
      title: "错误",
      description: "两次输入的密码不一致",
    });
    return;
  }

  loading.value = true;
  try {
    const response = await users.resetPassword({
      email: formData.email,
      newPassword: formData.newPassword,
      code: formData.code,
    });

    if (response.code === 200) {
      toast({
        title: "成功",
        description: "密码重置成功，请使用新密码登录",
      });
      router.push("/login");
    } else {
      toast({
        variant: "destructive",
        title: "重置失败",
        description: response.message || "密码重置失败",
      });
    }
  } catch (error: any) {
    toast({
      variant: "destructive",
      title: "重置失败",
      description: error.data?.message || "密码重置失败",
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
      <h2 class="text-3xl font-bold tracking-tight">重置密码</h2>
      <p class="mt-2 text-sm text-muted-foreground">
        {{ step === 1 ? "输入您的邮箱以接收验证码" : "输入验证码和新密码" }}
      </p>
    </div>

    <!-- 步骤1: 发送验证码 -->
    <form
      v-if="step === 1"
      @submit.prevent="handleSendCode"
      class="mt-8 space-y-6"
    >
      <div class="space-y-4">
        <div>
          <Label for="email">邮箱</Label>
          <Input
            id="email"
            v-model="formData.email"
            type="email"
            placeholder="请输入邮箱"
            :disabled="codeSending"
            class="mt-1"
          />
        </div>
      </div>

      <Button
        type="submit"
        class="w-full"
        :disabled="codeSending || countdown > 0 || !formData.email"
      >
        {{
          countdown > 0
            ? `${countdown}秒后重试`
            : codeSending
            ? "发送中..."
            : "发送验证码"
        }}
      </Button>
      <div class="text-center text-sm">
        <NuxtLink to="/login" class="text-primary hover:underline">
          返回登录
        </NuxtLink>
      </div>
    </form>

    <!-- 步骤2: 重置密码 -->
    <form v-else @submit.prevent="handleSubmit" class="mt-8 space-y-6">
      <div class="space-y-4">
        <div>
          <Label for="code">验证码</Label>
          <Input
            id="code"
            v-model="formData.code"
            type="text"
            placeholder="请输入邮箱验证码"
            :disabled="loading"
            class="mt-1"
          />
        </div>

        <div>
          <Label for="newPassword">新密码</Label>
          <Input
            id="newPassword"
            v-model="formData.newPassword"
            type="password"
            placeholder="6-20位"
            :disabled="loading"
            class="mt-1"
          />
        </div>

        <div>
          <Label for="confirmPassword">确认密码</Label>
          <Input
            id="confirmPassword"
            v-model="formData.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            :disabled="loading"
            class="mt-1"
          />
        </div>
      </div>

      <div class="flex gap-2">
        <Button
          type="button"
          variant="outline"
          @click="step = 1"
          :disabled="loading"
          class="flex-1"
        >
          上一步
        </Button>
        <Button type="submit" :disabled="loading" class="flex-1">
          {{ loading ? "重置中..." : "重置密码" }}
        </Button>
      </div>

      <div class="text-center text-sm">
        <NuxtLink to="/login" class="text-primary hover:underline">
          返回登录
        </NuxtLink>
      </div>
    </form>

    <!-- 图形验证码对话框 -->
    <CaptchaDialog
      v-model:open="captchaDialogShow"
      v-model:code="formData.captchaCode"
      v-model:uuid="formData.captchaUUID"
    />
  </div>
</template>
