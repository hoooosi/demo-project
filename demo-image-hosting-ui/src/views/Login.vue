<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { UserApi } from '@/api'
import { useRouter, useRoute } from 'vue-router'
import { MetaInfo } from '@/router'

const route = useRoute()
const router = useRouter()

const mode = ref('login')
const loading = ref(false)
const form = reactive({
  name: '',
  account: '',
  password: '',
  confirmPassword: '',
})
const formRules = {
  name: [
    { required: true, message: 'Please input your name', trigger: 'blur' },
    { min: 2, max: 10, message: 'Name length must be between 2 and 10', trigger: 'blur' },
  ],
  account: [
    { required: true, message: 'Please input your account', trigger: 'blur' },
    { min: 3, max: 20, message: 'Account length must be between 3 and 20', trigger: 'blur' },
  ],
  password: [
    { required: true, message: 'Please input your password', trigger: 'blur' },
    { min: 6, max: 20, message: 'Password length must be between 6 and 20', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: 'Please confirm your password', trigger: 'blur' },
    {
      validator: (rule: any, value: string) => {
        if (value !== form.password)
          return Promise.reject('Password and confirm password do not match')
        return Promise.resolve()
      },
      trigger: 'blur',
    },
  ],
}

async function handleLogin() {
  if (loading.value) return
  loading.value = true
  try {
    const req: Req.LoginReq = {
      account: form.account,
      password: form.password,
    }
    await UserApi.login(req)
    await UserApi.auth()
    message.success('LOGIN SUCCESS')
    router.push({ name: MetaInfo.Images.name })
  } catch (e: any) {
    message.error(e.message)
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  if (loading.value) return
  loading.value = true
  try {
    const req: Req.RegisterReq = {
      name: form.name,
      account: form.account,
      password: form.password,
    }
    await UserApi.register(req)
    await UserApi.auth()
    message.success('REGISTER SUCCESS')
    router.push({ name: MetaInfo.Images.name })
  } catch (error: any) {
    message.error(error.message)
  } finally {
    loading.value = false
  }
}

function goHome() {
  router.push('/')
}

onMounted(() => {
  const _mode = route.query.mode as string
  if (_mode === 'register') {
    mode.value = 'register'
  } else if (_mode === 'login') {
    mode.value = 'login'
  }
})
</script>
<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <h1>Image Hosting</h1>
        <p>Welcome back, please login to your account</p>
      </div>

      <a-tabs v-model:activeKey="mode" centered>
        <a-tab-pane key="login" tab="Login">
          <a-form :model="form" layout="vertical" @finish="handleLogin" :rules="formRules">
            <a-form-item name="account">
              <a-input v-model:value="form.account" placeholder="Please input account" size="large">
                <template #prefix>
                  <UserOutlined />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item name="password">
              <a-input-password
                v-model:value="form.password"
                placeholder="Please input password"
                size="large"
              >
                <template #prefix>
                  <LockOutlined />
                </template>
              </a-input-password>
            </a-form-item>

            <a-form-item>
              <a-button type="primary" html-type="submit" size="large" block :loading="loading">
                Login
              </a-button>
            </a-form-item>
          </a-form>
        </a-tab-pane>

        <a-tab-pane key="register" tab="Register">
          <a-form :model="form" layout="vertical" @finish="handleRegister" :rules="formRules">
            <a-form-item name="name">
              <a-input v-model:value="form.name" placeholder="Please input name" size="large">
                <template #prefix>
                  <UserOutlined />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item name="account">
              <a-input v-model:value="form.account" placeholder="Please input account" size="large">
                <template #prefix>
                  <UserOutlined />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item name="password">
              <a-input-password
                v-model:value="form.password"
                placeholder="Please input password"
                size="large"
              >
                <template #prefix>
                  <LockOutlined />
                </template>
              </a-input-password>
            </a-form-item>

            <a-form-item name="confirmPassword">
              <a-input-password
                v-model:value="form.confirmPassword"
                placeholder="Please input confirm password"
                size="large"
              >
                <template #prefix>
                  <LockOutlined />
                </template>
              </a-input-password>
            </a-form-item>

            <a-form-item>
              <a-button type="primary" html-type="submit" size="large" block :loading="loading">
                Register
              </a-button>
            </a-form-item>
          </a-form>
        </a-tab-pane>
      </a-tabs>

      <div class="login-footer">
        <a-button type="link" @click="goHome">Go to home page</a-button>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;

  .login-container {
    width: 100%;
    max-width: 400px;
    background: #fff;
    border-radius: 12px;
    padding: 40px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);

    .login-header {
      text-align: center;
      margin-bottom: 32px;

      h1 {
        font-size: 28px;
        font-weight: bold;
        color: #1890ff;
        margin-bottom: 8px;
      }

      p {
        color: #666;
        margin: 0;
      }
    }

    .ant-tabs {
      .ant-tabs-tab {
        font-size: 16px;
        font-weight: 500;
      }
    }

    .ant-form {
      .ant-form-item {
        margin-bottom: 20px;

        .ant-form-item-label {
          label {
            font-weight: 500;
            color: #333;
          }
        }
      }

      .ant-input-affix-wrapper {
        .anticon {
          color: #bbb;
        }
      }
    }

    .login-footer {
      text-align: center;
      margin-top: 24px;
      padding-top: 24px;
      border-top: 1px solid #f0f0f0;
    }
  }
}
</style>
