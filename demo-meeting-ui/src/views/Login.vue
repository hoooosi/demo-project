<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Loading } from '@element-plus/icons-vue'
import { UserApi } from '@/api'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const isLogin = ref(true)
const loading = ref(false)
const checkCodeLoading = ref(false)

const checkCodeInfo = ref<VO.CheckCodeVO>()
const loginForm = reactive<DTO.LoginDTO>({
  email: 'testemail@qq.com',
  password: '12345678',
})
const registerForm = reactive<DTO.RegisterDTO>({
  email: 'testemail@qq.com',
  password: '12345678',
  nickName: 'testuser',
  checkCode: '',
  checkCodeKey: '',
})

const loginRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
}

const registerRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
  nickName: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, message: '昵称长度不能少于2位', trigger: 'blur' },
  ],
  checkCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
}

const loginFormRef = ref()
const registerFormRef = ref()

const toggleMode = () => {
  isLogin.value = !isLogin.value
  if (loginFormRef.value) {
    loginFormRef.value.resetFields()
  }
  if (registerFormRef.value) {
    registerFormRef.value.resetFields()
  }
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const result = await UserApi.login(loginForm)
    localStorage.setItem('token', result.data)
    const userInfo = await UserApi.me()
    userStore.setUserInfo(userInfo.data)
    ElMessage.success('Login successful!')
    router.push('/dashboard')
  } catch (e: any) {
    ElMessage.error(e.message || 'Login failed')
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await UserApi.register(registerForm)
    ElMessage.success('Registration successful!')
    isLogin.value = true
  } catch (error: any) {
    ElMessage.error(error.message || 'Registration failed')
  } finally {
    loading.value = false
  }
}

// Get check code
const getCheckCode = async () => {
  checkCodeLoading.value = true
  try {
    const result = await UserApi.checkCode()
    checkCodeInfo.value = result.data
    registerForm.checkCodeKey = result.data.key
  } catch (error: any) {
    ElMessage.error(error.message || 'Getting check code failed')
  } finally {
    checkCodeLoading.value = false
  }
}

// Refresh check code
const refreshCheckCode = () => {
  getCheckCode()
}

// Check login status
const checkLoginStatus = async () => {
  const token = localStorage.getItem('token')
  if (token) {
    try {
      const result = await UserApi.me()
      userStore.setUserInfo(result.data)

      router.push('/dashboard')
    } catch (error) {
      userStore.clearUserInfo()
    }
  }
}

onMounted(() => {
  checkLoginStatus()
  getCheckCode()
})
</script>

<template>
  <div class="login-shell">
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <h1 class="login-title">{{ isLogin ? 'Login' : 'Register' }}</h1>
          <p class="login-subtitle">Welcome to the Meeting System</p>
        </div>

        <div class="login-form-wrapper">
          <!-- LOGIN FORM -->
          <el-form
            v-if="isLogin"
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
          >
            <el-form-item prop="email" class="form-item">
              <el-input
                v-model="loginForm.email"
                placeholder="Please enter your email"
                :prefix-icon="Message"
                size="large"
                class="form-input"
              />
            </el-form-item>

            <el-form-item prop="password" class="form-item">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="Please enter your password"
                :prefix-icon="Lock"
                size="large"
                show-password
                class="form-input"
              />
            </el-form-item>

            <el-form-item class="form-item form-submit">
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleLogin"
                class="submit-button"
              >
                {{ loading ? 'Logging in...' : 'Login' }}
              </el-button>
            </el-form-item>
          </el-form>

          <!-- REGISTER FORM -->
          <el-form
            v-else
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            class="register-form"
          >
            <el-form-item prop="email" class="form-item">
              <el-input
                v-model="registerForm.email"
                placeholder="Please enter your email"
                :prefix-icon="Message"
                size="large"
                class="form-input"
              />
            </el-form-item>

            <el-form-item prop="nickName" class="form-item">
              <el-input
                v-model="registerForm.nickName"
                placeholder="Please enter your nickname"
                :prefix-icon="User"
                size="large"
                class="form-input"
              />
            </el-form-item>

            <el-form-item prop="password" class="form-item">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="Please enter your password"
                :prefix-icon="Lock"
                size="large"
                show-password
                class="form-input"
              />
            </el-form-item>
            <el-form-item prop="checkCode" class="form-item">
              <div class="check-code-wrapper">
                <el-input
                  v-model="registerForm.checkCode"
                  placeholder="Please enter the verification code"
                  size="large"
                  class="check-code-input"
                />
                <div class="check-code-image-wrapper">
                  <img
                    v-if="checkCodeInfo"
                    :src="checkCodeInfo.base64"
                    alt="Verification Code"
                    class="check-code-image"
                    @click="refreshCheckCode"
                  />
                  <div v-else class="check-code-placeholder" @click="refreshCheckCode">
                    <el-icon v-if="checkCodeLoading" class="is-loading">
                      <Loading />
                    </el-icon>
                    <span v-else>Click to get</span>
                  </div>
                </div>
              </div>
            </el-form-item>

            <el-form-item class="form-item form-submit">
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleRegister"
                class="submit-button"
              >
                {{ loading ? 'Registering...' : 'Register' }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="login-footer">
          <p class="toggle-text">
            {{ isLogin ? "Don't have an account?" : 'Already have an account?' }}
            <el-button type="text" @click="toggleMode" class="toggle-button">
              {{ isLogin ? 'Register now' : 'Login now' }}
            </el-button>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.login-shell {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;

  .login-container {
    position: relative;
    z-index: 1;
    width: 100%;
    max-width: 440px;
    padding: 20px;

    .login-card {
      background: rgba(255, 255, 255, 0.98);
      border-radius: 24px;
      backdrop-filter: blur(20px);
      overflow: hidden;
      transition: all 0.3s ease;
      .login-header {
        text-align: center;
        padding: 48px 48px 32px;
        background: transparent;
        color: #2d3748;

        .login-title {
          font-size: 32px;
          font-weight: 800;
          margin: 0 0 12px;
          letter-spacing: -0.5px;
          background: linear-gradient(135deg, #667eea, #764ba2);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }

        .login-subtitle {
          font-size: 16px;
          margin: 0;
          color: #64748b;
          font-weight: 400;
        }
      }
      .login-form-wrapper {
        padding: 0 48px 32px;

        .login-form,
        .register-form {
          .form-item {
            margin-bottom: 28px;

            .form-input {
              :deep(.el-input__wrapper) {
                border-radius: 16px;
                border: 1px solid #e2e8f0;
                box-shadow: none;
                transition: all 0.3s ease;
                padding: 0 24px;
                height: 56px;
                background: rgba(248, 250, 252, 0.8);

                &:hover {
                  border-color: #667eea;
                  background: rgba(248, 250, 252, 1);
                }

                &.is-focus {
                  border-color: #667eea;
                  background: rgba(248, 250, 252, 1);
                }
              }

              :deep(.el-input__inner) {
                font-size: 16px;
                font-weight: 500;
                color: #2d3748;

                &::placeholder {
                  color: #94a3b8;
                  font-weight: 400;
                }
              }
            }

            .check-code-wrapper {
              display: flex;
              gap: 16px;

              .check-code-input {
                flex: 1;

                :deep(.el-input__wrapper) {
                  border-radius: 16px;
                  border: 1px solid #e2e8f0;
                  box-shadow: none;
                  transition: all 0.3s ease;
                  padding: 0 24px;
                  height: 56px;
                  background: rgba(248, 250, 252, 0.8);

                  &:hover {
                    border-color: #667eea;
                    background: rgba(248, 250, 252, 1);
                  }

                  &.is-focus {
                    border-color: #667eea;
                    background: rgba(248, 250, 252, 1);
                  }
                }
              }

              .check-code-image-wrapper {
                width: 120px;
                height: 56px;
                border-radius: 16px;
                border: 1px solid #e2e8f0;
                background: rgba(248, 250, 252, 0.8);
                display: flex;
                align-items: center;
                justify-content: center;
                cursor: pointer;
                transition: all 0.3s ease;
                overflow: hidden;

                &:hover {
                  border-color: #667eea;
                  background: rgba(248, 250, 252, 1);
                }

                .check-code-image {
                  width: 100%;
                  height: 100%;
                  object-fit: cover;
                  cursor: pointer;
                }

                .check-code-placeholder {
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  width: 100%;
                  height: 100%;
                  color: #64748b;
                  font-size: 14px;
                  font-weight: 500;
                  cursor: pointer;

                  .is-loading {
                    animation: rotate 1s linear infinite;
                  }
                }
              }
            }
            &.form-submit {
              margin-bottom: 0;
              margin-top: 40px;

              .submit-button {
                width: 100%;
                height: 56px;
                font-size: 16px;
                font-weight: 600;
                border-radius: 16px;
                background: linear-gradient(135deg, #667eea, #764ba2);
                border: none;
                transition: all 0.3s ease;

                &:hover {
                  transform: translateY(-2px);
                  filter: brightness(1.1);
                }

                &:active {
                  transform: translateY(0);
                }
              }
            }
          }
        }
      }
      .login-footer {
        padding: 24px 48px 48px;
        text-align: center;
        background: transparent;

        .toggle-text {
          margin: 0;
          color: #64748b;
          font-size: 15px;
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 8px;

          .toggle-button {
            padding: 0;
            font-size: 15px;
            font-weight: 600;
            color: #667eea;
            transition: all 0.3s ease;

            &:hover {
              color: #764ba2;
            }
          }
        }
      }
    }
  }
}
@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
