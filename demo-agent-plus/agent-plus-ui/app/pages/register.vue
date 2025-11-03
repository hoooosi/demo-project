<script setup lang="ts">
definePageMeta({
  layout: "auth",
});

const { toast } = useToast();
const { users } = useApi();
const router = useRouter();

const captchaDialogShow = ref(false);
const loading = ref(false);
const codeSending = ref(false);
const codeSent = ref(false);
const countdown = ref(0);

const formData = reactive({
  email: "",
  password: "",
  confirmPassword: "",
  code: "",
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

/** 监听邮箱变化，重置验证码状态 */
watch(
  () => formData.email,
  () => {
    codeSent.value = false;
    countdown.value = 0;
  }
);

/** 监听对话框关闭，发送验证码 */
watch(captchaDialogShow, async (newVal, oldVal) => {
  // 对话框从打开变为关闭，且已填写验证码，则发送邮箱验证码
  if (oldVal && !newVal && formData.captchaCode && formData.captchaUUID) {
    await sendEmailCode();
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

/** 验证密码 */
const validatePassword = (): boolean => {
  if (!formData.password) {
    toast({
      variant: "destructive",
      title: "错误",
      description: "请输入密码",
    });
    return false;
  }

  if (formData.password.length < 6 || formData.password.length > 20) {
    toast({
      variant: "destructive",
      title: "错误",
      description: "密码长度应为6-20个字符",
    });
    return false;
  }

  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]+$/;
  if (!passwordRegex.test(formData.password)) {
    toast({
      variant: "destructive",
      title: "错误",
      description: "密码必须包含字母和数字",
    });
    return false;
  }

  if (formData.password !== formData.confirmPassword) {
    toast({
      variant: "destructive",
      title: "错误",
      description: "两次输入的密码不一致",
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

/** 发送邮箱验证码 */
const sendEmailCode = async () => {
  codeSending.value = true;
  try {
    const response = await users.sendEmailCode({
      email: formData.email,
      captchaUuid: formData.captchaUUID,
      captchaCode: formData.captchaCode,
    });

    if (response.code === 200) {
      toast({
        title: "成功",
        description: "验证码已发送到您的邮箱",
      });
      codeSent.value = true;
      countdown.value = 60;
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

/** 提交注册 */
const handleSubmit = async () => {
  if (!validateEmail(formData.email)) {
    return;
  }

  if (!validatePassword()) {
    return;
  }

  if (!formData.code) {
    toast({
      variant: "destructive",
      title: "错误",
      description: "请输入邮箱验证码",
    });
    return;
  }

  if (!codeSent.value) {
    toast({
      variant: "destructive",
      title: "错误",
      description: "请先发送邮箱验证码",
    });
    return;
  }

  loading.value = true;
  try {
    const response = await users.register({
      email: formData.email,
      password: formData.password,
      code: formData.code,
    });

    if (response.code === 200) {
      toast({
        title: "成功",
        description: "注册成功，请登录",
      });
      router.push("/login");
    } else {
      toast({
        variant: "destructive",
        title: "注册失败",
        description: response.message || "注册失败，请重试",
      });
    }
  } catch (error: any) {
    toast({
      variant: "destructive",
      title: "注册失败",
      description: error.data?.message || "注册失败，请重试",
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
      <h2 class="text-3xl font-bold tracking-tight">注册账号</h2>
      <p class="mt-2 text-sm text-muted-foreground">创建您的 Agent-Plus 账户</p>
    </div>

    <form @submit.prevent="handleSubmit" class="mt-8 space-y-6">
      <div class="space-y-4">
        <!-- 邮箱 -->
        <div>
          <Label for="email">邮箱 *</Label>
          <Input
            id="email"
            v-model="formData.email"
            type="email"
            placeholder="请输入邮箱"
            class="mt-1"
          />
        </div>

        <!-- 邮箱验证码 -->
        <div>
          <Label for="code">邮箱验证码 *</Label>
          <div class="flex gap-2 mt-1">
            <Input
              id="code"
              v-model="formData.code"
              type="text"
              placeholder="请输入邮箱验证码"
              class="flex-1"
            />
            <Button
              type="button"
              variant="outline"
              @click="handleSendCode"
              :disabled="codeSending || countdown > 0 || !formData.email"
              class="w-32"
            >
              {{
                countdown > 0
                  ? `${countdown}秒后重试`
                  : codeSent
                  ? "重新发送"
                  : "发送验证码"
              }}
            </Button>
          </div>
        </div>

        <!-- 密码 -->
        <div>
          <Label for="password">密码 *</Label>
          <Input
            id="password"
            v-model="formData.password"
            type="password"
            placeholder="6-20位，包含字母和数字"
            class="mt-1"
          />
        </div>

        <!-- 确认密码 -->
        <div>
          <Label for="confirmPassword">确认密码 *</Label>
          <Input
            id="confirmPassword"
            v-model="formData.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            class="mt-1"
          />
        </div>
      </div>
      <Button
        type="submit"
        class="w-full"
        :disabled="loading || !codeSent || !formData.code"
      >
        {{ loading ? "注册中..." : "注册" }}
      </Button>

      <div class="text-center text-sm">
        <span class="text-muted-foreground">已有账号？</span>
        <NuxtLink to="/login" class="text-primary hover:underline ml-1">
          立即登录
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
