<script setup lang="ts">
const { users } = useApi();
const { toast } = useToast();

const open = defineModel<boolean>("open", { required: true });
const captchaCode = defineModel<string>("code", { required: true });
const captchaUUID = defineModel<string>("uuid", { required: true });

const captcha = reactive({
  uuid: "",
  imageBase64: "",
});
const countdown = ref(60);
const loading = ref(false);
let timer: NodeJS.Timeout | null = null;

/** 获取图形验证码 */
const fetchCaptcha = async () => {
  loading.value = true;
  try {
    const response = await users.getCaptcha();
    captcha.uuid = response.data.uuid;
    captcha.imageBase64 = response.data.imageBase64;
    captchaUUID.value = captcha.uuid;
  } catch (error: any) {
    toast({
      variant: "destructive",
      title: "错误",
      description: error.message || "获取验证码失败",
    });
    return false;
  } finally {
    loading.value = false;
  }
};

/** 刷新验证码 */
const handleRefresh = () => {
  fetchCaptcha();
};

/** 关闭对话框 */
const close = () => {
  captchaCode.value = "";
  countdown.value = 60;
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
  open.value = false;
};

/** 启动倒计时 */
const startCountdown = () => {
  countdown.value = 60;
  if (timer) {
    clearInterval(timer);
  }
  timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      fetchCaptcha();
      countdown.value = 60;
    }
  }, 1000);
};

/** 监听打开状态，打开时清空输入并启动倒计时 */
watch(open, async (newVal) => {
  if (newVal) {
    captchaCode.value = "";
    await fetchCaptcha();
    startCountdown();
  } else {
    if (timer) {
      clearInterval(timer);
      timer = null;
    }
  }
});

/** 组件卸载时清理定时器 */
onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
});
</script>

<template>
  <Dialog :open="open">
    <div class="p-6">
      <div class="space-y-4">
        <div>
          <div class="flex gap-2 mt-2">
            <Input
              id="dialog-captcha"
              v-model="captchaCode"
              type="text"
              placeholder="请输入图形验证码"
              :disabled="loading"
              class="flex-1"
              autofocus
            />
            <!-- 验证码图片 -->
            <div
              v-if="captcha.imageBase64"
              @click="handleRefresh"
              class="cursor-pointer border rounded-md overflow-hidden hover:border-primary transition-colors flex-shrink-0"
              :class="{ 'opacity-50': loading }"
              title="点击刷新验证码"
            >
              <img
                :src="captcha.imageBase64"
                alt="验证码"
                class="h-10 w-28 object-contain"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 按钮 -->
      <div class="flex gap-2 mt-6">
        <Button
          type="button"
          variant="outline"
          class="flex-1"
          @click="close"
          :disabled="loading"
        >
          取消
        </Button>
        <Button
          type="button"
          class="flex-1"
          @click="open = false"
          :disabled="loading || !captchaCode.trim()"
        >
          确认
        </Button>
      </div>
    </div>
  </Dialog>
</template>
